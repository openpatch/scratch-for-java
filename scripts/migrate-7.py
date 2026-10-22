#!/usr/bin/env python3
"""Moves Scratch for Java 6.x code to 7.0.

    python3 scripts/migrate-7.py <folder or file>...            dry run: what would change
    python3 scripts/migrate-7.py --write <folder or file>...    change the files in place
    python3 scripts/migrate-7.py --out <dir> <folder>           write migrated copies to <dir>
                                                                (other files are copied as they are)

Mechanical changes are made; everything that needs a person gets a comment
`// TODO 7.0: …` above the line, with a hint. Afterwards: compile, fix the TODOs, run.
"""

import argparse
import difflib
import re
import shutil
import sys
from pathlib import Path

TODO = "// TODO 7.0: "

# ---- packages ---------------------------------------------------------------------------

IMPORTS = [
    (r"import org\.openpatch\.scratch\.extensions\.camera\.\*;\n", ""),
    (r"org\.openpatch\.scratch\.extensions\.camera\.Camera", "org.openpatch.scratch.Camera"),
    (r"import org\.openpatch\.scratch\.extensions\.shader\.\*;\n", ""),
    (r"org\.openpatch\.scratch\.extensions\.shader\.(Shaders?)", r"org.openpatch.scratch.\1"),
    (r"import org\.openpatch\.scratch\.extensions\.pixels\.\*;\n", ""),
    (r"org\.openpatch\.scratch\.extensions\.pixels\.Pixels", "org.openpatch.scratch.Pixels"),
    (r"import org\.openpatch\.scratch\.extensions\.sorting\.[\w*]+;\n", ""),
    (r"import org\.openpatch\.scratch\.extensions\.sound\.\*;\n", ""),
    (r"org\.openpatch\.scratch\.extensions\.sound\.SoundPlayback", "org.openpatch.scratch.SoundPlayback"),
    (r"org\.openpatch\.scratch\.extensions\.tiled", "org.openpatch.scratch.tiled"),
    (r"org\.openpatch\.scratch\.extensions\.fs", "org.openpatch.scratch.save"),
    (r"org\.openpatch\.scratch\.extensions\.recorder", "org.openpatch.scratch.recorder"),
    (r"org\.openpatch\.scratch\.extensions\.drawing", "org.openpatch.scratch.drawing"),
    (r"import org\.openpatch\.scratch\.extensions\.text\.\*;\n", "import org.openpatch.scratch.drawing.*;\n"),
    (r"org\.openpatch\.scratch\.extensions\.text\.BitmapText", "org.openpatch.scratch.drawing.BitmapText"),
    (r"import org\.openpatch\.scratch\.extensions\.math\.\*;\n", ""),
    (r"import org\.openpatch\.scratch\.extensions\.color\.\*;\n", ""),
    (r"import org\.openpatch\.scratch\.extensions\.timer\.\*;\n", ""),
    (r"import org\.openpatch\.scratch\.extensions\.hitbox\.\*;\n", ""),
    (r"import org\.openpatch\.scratch\.extensions\.shape\.\*;\n", ""),
    (r"import org\.openpatch\.scratch\.extensions\.animation\.\*;\n", ""),
    (r"import org\.openpatch\.scratch\.extensions\.text\.Text;\n", ""),
    (r"import org\.openpatch\.scratch\.TextureSampling;\n", ""),
]

# ---- classes and simple renames ---------------------------------------------------------

RENAMES = [
    (r"\bremoveAllSprites\(\)", "remove(Sprite.class)"),
    (r"\bextends AnimatedSprite\b", "extends Sprite"),
    (r"\bnew AnimatedSprite\(", "new Sprite("),
    (r"\bAnimatedSprite\b", "Sprite"),
    (r"\.pointTowardsSprite\(", ".pointTowards("),
    (r"\bpointTowardsSprite\(", "pointTowards("),
    (r"\.stampLayerToBackground\(([^)]*)\)", r".showLayer(\1, Layer.BACKGROUND)"),
    (r"\.stampLayerToForeground\(([^)]*)\)", r".showLayer(\1, Layer.FOREGROUND)"),
    (r"Window\.useTextureSampling\([^)]*\);", "Window.usePixelArt();"),
    (r"\.setIsUI\(true\)", ".setLayer(Layer.UI)"),
    (r"\bsetIsUI\(true\)", "setLayer(Layer.UI)"),
    (r"\bsetUI\(true\)", "setLayer(Layer.UI)"),
    (r"\.goToForeground\(\)", ".setLayer(Layer.FOREGROUND)"),
    (r"\.goToBackground\(\)", ".setLayer(Layer.BACKGROUND)"),
    (r"\.getSorting\(\)\.byY\(\)", ".sortByY()"),
    (r"\bgetSorting\(\)\.byY\(\)", "sortByY()"),
    (r"\.getSorting\(\)\.off\(\)", ".stopSorting()"),
    (r"\.getSorting\(\)\.by\(", ".sortBy("),
    (r"\bgetSorting\(\)\.by\(", "sortBy("),
    (r"\bWindow\.isTransitioning\(\)", "Window.getInstance().isTransitioning()"),
    (r"\bwhenMouseWheelMoved\(int ", "whenMouseWheelMoved(double "),
    (r"\.forMillis\(", ".during("),
    (r"\bgetShaders\(\)\.add\(([^,]+),\s*([^,]+),\s*(?:null|\"[^\"]*\")\)", r"getShaders().add(\1, \2)"),
    (r"\bthis\.clone\(\)", "this.createClone()"),
    (r"\bpointInDirection\((-?[\d.]+)\)", r"setDirection(\1)"),
    (r"\.isUI\(\)", ".getLayer().equals(Layer.UI)"),
    (r"\.isInBackground\(\)", ".getLayer().equals(Layer.BACKGROUND)"),
    # a UI sprite is a sprite in the UI layer
    (r"(?<!import )\borg\.openpatch\.scratch\.UISprite\b", "UISprite"),
    (r"(\n[ \t]*)(UISprite|var|final var) (\w+) = new UISprite\(\);",
     lambda m: f"{m[1]}{'Sprite' if m[2] == 'UISprite' else m[2]} {m[3]} = new Sprite();{m[1]}{m[3]}.setLayer(Layer.UI);"),
    (r"(\n[ \t]*)((?:this\.)?\w+(?:\[[^\]\n]+\])?) = new UISprite\(\);", r"\1\2 = new Sprite();\1\2.setLayer(Layer.UI);"),
    (r"\b(\w+) instanceof UISprite\b", r"\1.getLayer() == Layer.UI"),
    # declarations without a value; a field with `= new UISprite()` is left to a person
    (r"\bUISprite(\[\])?(\s+\w+\s*;)", r"Sprite\1\2"),
    (r"\bnew UISprite\[", "new Sprite["),
    (r"\bUISprite\[\](\s+\w+\s*=\s*new Sprite\[)", r"Sprite[]\1"),
    # 6.x's toLocal* took a screen point to the world, toGlobal* the world to the screen
    (r"\.toLocal(X|Y|Position)\(", r".toWorld\1("),
    (r"\.toGlobal(X|Y|Position)\(", r".toScreen\1("),
]

# calls whose last (or only) numeric argument changes from milliseconds to seconds
SECONDS = {
    "wait": "waitSeconds",
    "everyMillis": "every",
    "during": "during",
    "afterMillis": "after",
    "setAnimationInterval": "setAnimationInterval",
}

LAST_ARG_SECONDS = ["say", "think", "display", "showText", "transitionToStage"]

TODO_PATTERNS = [
    (r"\bremoveAll(Texts|Pens)\(\)", "removeAllTexts/removeAllPens are gone: remove(text) / remove(pen) each, or removeAll() for sprites, texts and pens."),
    (r"\bgetDeltaTime\(\)", "getDeltaTime() is gone: every tick is 1/60 s of game time."),
    (r"\bgetFrameRate\(\)", "getFrameRate() is gone: frames and ticks differ; F12 shows the frame rate."),
    (r"\bgetTimer\(\"", "named timers are gone: keep a Timer in a field (new Timer())."),
    (r"\bTimer\.millis\(\)", "Timer.millis() is gone: use getTimer().getSeconds(); shaders get `time` themselves."),
    (r"\bpointInDirection\(", "pointInDirection is gone: setDirection(degrees) or pointTowards(position)."),
    (r"\bsetDirection\(new Vector2", "setDirection(Vector2) is gone: pointTowards(position) or setDirection(vector.direction())."),
    # one argument, also a call like pickRandom(0, 255); not new Color(…), which 7.0 keeps
    (r"\bsetTint\((?!\s*new\s+Color\b)(?:[^(),;]|\([^();]*\))+\)", "setTint(hue) is gone. Scratch's colour effect: setColorEffect(…), which turns the hue (200 is a full turn, so 6.x numbers do not carry over) and looks lighter. The 6.x look exactly: setTint(Color.fromHSB(hue / 2.55, 100, 100))."),
    (r"(?<![Ww]indow\.)(?<!getInstance\(\)\.)\bexit\(\)", "Stage.exit() is gone: Window.getInstance().exit()."),
    (r"\bmove\(\s*new Vector2", "move(Vector2) is gone: setDirection(v.direction()); move(v.length());"),
    (r"\bchangeTint\(", "changeTint is gone: changeColorEffect (Scratch's colour effect; 200 is a full turn, 6.x's 255 was)."),
    # not a pen's changeColor, which 7.0 keeps
    (r"(?<!getPen\(\))(?<![Pp]en)\.changeColor\(", "Color is immutable: color = color.withHue(color.getHue() + …)."),
    (r"\.set(?:RGB|HSB)\(", "Color is immutable: make a new Color or use withRed/withHue/…"),
    (r"\.get(?:H|S|L|HSB)\(\)", "Color's getters are getHue/getSaturation/getBrightness (0–100)."),
    (r"\bnew Color\(\s*[\w.]+\s*\)", "new Color(hue) is gone: Color.fromHSB(hue, 100, 100) (hue 0–100)."),
    (r"\bUISprite\b", "UISprite is gone: new Sprite() and setLayer(Layer.UI) (for a field, in the constructor)."),
    (r"\bgetSorting\(\)", "Sorting is gone: sortByY(), sortBy(comparator) and stopSorting() on the stage."),
    (r"\baddAnimation\([^;]*->", "addAnimation with a function is gone: numbered files (\"walk%d.png\" and a count from 1, or from/to numbers: addAnimation(name, \"hit%d.png\", 0, 4)) or a sprite sheet."),
    (r"\bint\s+\w+\s*=[^;]*\bget(?:Width|Height)\(\)", "getWidth()/getHeight() are double now: keep a double, or (int) Math.round(…)."),
    (r"\bgetPixels\(\)", "getPixels() is gone: Pixels.of(stage)."),
    (r"\bText\.useFontSizes\(", "Text.useFontSizes is gone: sizes are made as needed."),
    (r"\bText\.useFont\([^,()]+,", "Text.useFont(path, n) now gives a pixel font's grid (the size of one font pixel), not a default size: use Text.useFont(path) and setTextSize on each text."),
    (r"\bText\.useSmoothing\(", "Text.useSmoothing is gone: Window.usePixelArt() switches smoothing off everywhere."),
    (r"\bKeyCode\.fromCode\(|\.getCode\(\)", "KeyCode has no AWT numbers any more."),
    (r"\bDEBUG_COLOR\b", "Window.DEBUG_COLOR is gone."),
    (r"\bgetPShader\(\)|\bPGraphics\b|\bPImage\b|processing\.", "Processing is gone in 7.0."),
    # sprites only: Vector2.clone() stays
    (r"(?<![Vv]ector)\.clone\(\)", "clone() is now createClone(), which needs no cast: Bullet b = this.createClone();"),
    (r"\baddAnimation\((?!\s*new\b)[^;]*,[^;]*,[^;]*,[^;]*,[^;]*,[^;]*,", "addAnimation with a row or column: name, sheet, width, height, then the frame numbers (row by row from 0)."),
    (r"\bshowLayer\(", "the map layer is now drawn every frame: remove eraseAll() and showing it again in run()."),
    (r"\bgetShaders\(\)\.add\(", "shaders have a new format (body only, `costume`, `uv`, `color`): see the shader docs."),
]


def seconds(arg):
    """A millisecond argument as seconds: literals divided, expressions wrapped."""
    a = arg.strip()
    if re.fullmatch(r"\d+", a):
        v = int(a) / 1000
        return repr(v) if v != int(v) else str(int(v))
    if re.fullmatch(r"\d+\.\d*", a):
        return repr(float(a) / 1000)
    return f"({a}) / 1000.0"


def split_args(s):
    """Splits a call's argument list at top-level commas."""
    args, depth, cur, quote = [], 0, "", None
    for ch in s:
        if quote:
            cur += ch
            if ch == quote and not cur.endswith("\\" + quote):
                quote = None
            continue
        if ch in "\"'":
            quote = ch
        elif ch in "([{":
            depth += 1
        elif ch in ")]}":
            depth -= 1
        elif ch == "," and depth == 0:
            args.append(cur)
            cur = ""
            continue
        cur += ch
    args.append(cur)
    return args


def rewrite_calls(text, name, transform):
    """Finds calls `name(` and lets transform(args) return the new call text or None."""
    out, i = [], 0
    pattern = re.compile(r"(?<![\w$])" + re.escape(name) + r"\(")
    while True:
        m = pattern.search(text, i)
        if not m:
            out.append(text[i:])
            break
        start, j, depth = m.start(), m.end(), 1
        while j < len(text) and depth:
            if text[j] == "(":
                depth += 1
            elif text[j] == ")":
                depth -= 1
            j += 1
        inner = text[m.end():j - 1]
        new = transform(split_args(inner)) if depth == 0 else None
        out.append(text[i:start])
        out.append(new if new is not None else text[start:j])
        i = j
    return "".join(out)


WINDOWS = ("window.", "myWindow.", "getInstance().", "Window.getInstance().")


def window_debug(m):
    """Debug mode lives on the window: stage.setDebug(…) and this.setDebug(…) go there."""
    receiver = m.group(1) or ""
    if receiver.endswith(WINDOWS) or receiver in WINDOWS:
        return m.group(0)
    return "Window.getInstance()." + m.group(2)



def add_import(text, cls):
    """import org.openpatch.scratch.<cls>; at the top of the file's imports (after the package)."""
    line = f"import org.openpatch.scratch.{cls};\n"
    first = re.search(r"^import [^\n]*\n", text, re.M)
    if first:
        return text[: first.start()] + line + text[first.start():]
    return re.sub(r"(package [\w.]+;\n)", lambda m: m.group(1) + "\n" + line, text, count=1)


def ui_sprites(text):
    """class X extends UISprite → a Sprite whose constructors start in the UI layer."""
    text = re.sub(r"[ \t]*// TODO 7\.0: UISprite is gone[^\n]*\n(?=[ \t]*(?:import org\.openpatch\.scratch\.UISprite;|(?:public |final |abstract )*class \w+ extends UISprite))", "", text)
    has_sprite = re.search(r"import org\.openpatch\.scratch\.(?:Sprite|\*);", text)
    text = text.replace("import org.openpatch.scratch.UISprite;\n", "" if has_sprite else "import org.openpatch.scratch.Sprite;\n")
    for name in re.findall(r"class (\w+) extends UISprite\b", text):
        text = text.replace(f"class {name} extends UISprite", f"class {name} extends Sprite")
        def start_in_ui(m):
            body = m.group(0)
            sup = re.match(r"(\{\s*\n(\s*)super\([^;]*\);\n)", body[body.index("{"):])
            head = body[: body.index("{")]
            if sup:
                return head + sup.group(1) + sup.group(2) + "this.setLayer(Layer.UI);\n"
            indent = re.search(r"\n(\s*)", body[body.index("{"):])
            return head + "{\n" + (indent.group(1) if indent else "    ") + "this.setLayer(Layer.UI);\n"
        text = re.sub(r"(?:public |protected |private )?" + name + r"\([^)]*\)\s*\{\s*\n(?:\s*super\([^;]*\);\n)?", start_in_ui, text)
    if "extends Sprite" in text and "import org.openpatch.scratch.*;" not in text and "import org.openpatch.scratch.Sprite;" not in text:
        text = add_import(text, "Sprite")
    return text


def migrate(text):
    for pattern, replacement in IMPORTS + RENAMES:
        text = re.sub(pattern, replacement, text)
    text = re.sub(r"((?:[\w.]+(?:\(\))?\.)?)\b(setDebug\(|isDebug\(\))", window_debug, text)
    text = ui_sprites(text)

    # (Type) x.clone() → x.createClone()
    text = re.sub(r"\(\s*\w+\s*\)\s*([\w.]+)\.clone\(\)", r"\1.createClone()", text)

    # calls of removed settings that have nothing to replace them: gone
    text = re.sub(r"\n[ \t]*(?://\s*TODO 7\.0:[^\n]*\n[ \t]*)?Text\.(?:useFontSizes|useSmoothing)\([^;]*\);", "", text)
    # time and resolution are filled in for every shader now
    text = re.sub(r"\n[ \t]*(?://\s*TODO 7\.0:[^\n]*\n[ \t]*)?[\w.]+\.set\(\"(?:time|resolution)\"[^;]*\);", "", text)

    # intervalMillis: seconds; skipping the first phase is the negation with the phases swapped
    def interval(m):
        receiver, args = m.group(1), split_args(m.group(2))
        args = [a.strip() for a in args]
        if len(args) == 1:
            return f"{receiver}interval({seconds(args[0])})"
        if len(args) == 2 and args[1] in ("true", "false"):
            call = f"{receiver}interval({seconds(args[0])})"
            return "!" + call if args[1] == "true" else call
        if len(args) == 2:
            return f"{receiver}interval({seconds(args[0])}, {seconds(args[1])})"
        if len(args) == 3:
            if args[2] == "true":
                return f"!{receiver}interval({seconds(args[1])}, {seconds(args[0])})"
            return f"{receiver}interval({seconds(args[0])}, {seconds(args[1])})"
        return m.group(0)
    text = re.sub(r"([\w.]*(?:\(\))?\.)intervalMillis\(([^()]*)\)", interval, text)

    # pen hues ran from 0 to 255 in 6.x; 7.0 uses Scratch's 0 to 100
    text = re.sub(r"getPen\(\)\.setColor\((\d+(?:\.\d+)?)\)",
                  lambda m: f"getPen().setColor({round(float(m.group(1)) * 100 / 255)})", text)

    # setHitbox(x0, y0, x1, y1, …) → setHitbox(new Polygon(xs, ys))
    def hitbox(args):
        if len(args) >= 6 and len(args) % 2 == 0 and all(re.fullmatch(r"\s*-?[\d.]+\s*", a) for a in args):
            xs = ", ".join(a.strip() for a in args[0::2])
            ys = ", ".join(a.strip() for a in args[1::2])
            return f"setHitbox(new Polygon(new double[] {{{xs}}}, new double[] {{{ys}}}))"
        return None
    text = rewrite_calls(text, "setHitbox", hitbox)

    # milliseconds → seconds
    for old, new in SECONDS.items():
        def to_seconds(args, new=new, old=old):
            if old == "wait" and len(args) != 1:
                return None
            if not args or not args[0].strip():
                return None
            return f"{new}({seconds(args[-1])})" if len(args) == 1 else None
        text = rewrite_calls(text, old, to_seconds)
    for name in LAST_ARG_SECONDS:
        def last(args, name=name):
            if len(args) < 2:
                return None
            if name in ("say", "think", "display", "showText") and len(args) != 2:
                return None
            return f"{name}({', '.join(a.strip() for a in args[:-1])}, {seconds(args[-1])})"
        text = rewrite_calls(text, name, last)

    # addAnimation(name, sheet, frames, w, h) → addAnimation(name, sheet, w, h, 0, 1, …)
    def animation(args):
        if len(args) == 5 and re.fullmatch(r"\s*\d+\s*", args[2]):
            frames = int(args[2])
            numbers = ", ".join(str(i) for i in range(frames))
            return f"addAnimation({args[0].strip()}, {args[1].strip()}, {args[3].strip()}, {args[4].strip()}, {numbers})"
        return None
    text = rewrite_calls(text, "addAnimation", animation)

    # TODO markers above lines that need a person
    lines = text.split("\n")
    out = []
    for line in lines:
        notes = []
        for pattern, hint in TODO_PATTERNS:
            if re.search(pattern, line) and TODO + hint not in "\n".join(out[-3:]):
                notes.append(hint)
        indent = line[: len(line) - len(line.lstrip())]
        for hint in notes:
            out.append(f"{indent}{TODO}{hint}")
        out.append(line)
    text = "\n".join(out)

    # files importing single classes need the core classes the script brought in
    for cls in ("Layer", "Window", "Polygon", "Sprite"):
        if (re.search(r"\b" + cls + r"[.(]", text) and "import org.openpatch.scratch.*;" not in text
                and f"import org.openpatch.scratch.{cls};" not in text):
            text = add_import(text, cls)
    return text


def migrate_shader(text):
    """A Processing fragment shader → the 7.0 body format."""
    if "gl_FragColor" not in text and "vertTexCoord" not in text:
        return text  # already a 7.0 body, or not a shader of ours
    # the shader's own variables called `color` would clash with the header's output
    text = re.sub(r"\bcolor\b", "color_", text)
    drop = [
        r"^\s*#version[^\n]*\n",
        r"^\s*#ifdef GL_ES\s*\n(?:\s*precision[^\n]*\n)*\s*#endif\s*\n",
        r"^\s*precision[^\n]*\n",
        r"^\s*#define PROCESSING_\w+[^\n]*\n",
        r"^\s*(?:varying|in) vec[24] vertTexCoord;[^\n]*\n",
        r"^\s*(?:varying|in) vec4 vertColor;[^\n]*\n",
        r"^\s*uniform sampler2D texture;[^\n]*\n",
        r"^\s*uniform float time;[^\n]*\n",
        r"^\s*uniform vec2 resolution;[^\n]*\n",
        r"^\s*uniform vec2 texOffset;[^\n]*\n",
    ]
    for pattern in drop:
        text = re.sub(pattern, "", text, flags=re.MULTILINE)
    text = re.sub(r"\bvertTexCoord\.(?:st|xy)\b", "uv", text)
    text = re.sub(r"\bvertTexCoord\b", "vec4(uv, 0.0, 1.0)", text)
    text = re.sub(r"\btexture2D\s*\(\s*texture\b", "texture(costume", text)
    text = re.sub(r"\btexture\s*\(\s*texture\b", "texture(costume", text)
    text = re.sub(r"\btextureSize\s*\(\s*texture\b", "textureSize(costume", text)
    text = re.sub(r"\btexOffset\b(?:\.st)?", "(1.0 / resolution)", text)
    text = re.sub(r"\bgl_FragColor\b", "color", text)
    # `vec2 uv = vertTexCoord.xy;` became `vec2 uv = uv;`: a local hiding the input, uninitialised
    text = re.sub(r"^\s*vec2 uv = uv;[^\n]*\n", "", text, flags=re.MULTILINE)
    return text.lstrip("\n")


JAVA_BLOCK = re.compile(r"(```java[^\n]*\n)(.*?)(```)", re.DOTALL)


def migrate_markdown(text):
    """Every ```java block of a page through migrate(); the prose is left alone."""
    return JAVA_BLOCK.sub(lambda m: m.group(1) + migrate(m.group(2)) + m.group(3), text)


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("paths", nargs="+")
    parser.add_argument("--write", action="store_true", help="change files in place")
    parser.add_argument("--out", help="write migrated copies into this folder")
    args = parser.parse_args()
    changed = todos = 0
    for root in map(Path, args.paths):
        files = [root] if root.is_file() else sorted(p for p in root.rglob("*") if p.is_file())
        for f in files:
            rel = f.relative_to(root) if root.is_dir() else Path(f.name)
            target = Path(args.out) / rel if args.out else None
            if f.suffix in (".frag", ".glsl"):
                old = f.read_text(encoding="utf-8")
                new = migrate_shader(old)
                if new != old:
                    changed += 1
                    if not args.write and not args.out:
                        sys.stdout.writelines(difflib.unified_diff(old.splitlines(True), new.splitlines(True), str(f), str(f)))
                if args.write and new != old:
                    f.write_text(new, encoding="utf-8")
                if target:
                    target.parent.mkdir(parents=True, exist_ok=True)
                    target.write_text(new, encoding="utf-8")
                continue
            if f.suffix == ".md":
                old = f.read_text(encoding="utf-8")
                new = migrate_markdown(old)
                todos += new.count(TODO) - old.count(TODO)
                if new != old:
                    changed += 1
                    if not args.write and not args.out:
                        sys.stdout.writelines(difflib.unified_diff(old.splitlines(True), new.splitlines(True), str(f), str(f)))
                if args.write and new != old:
                    f.write_text(new, encoding="utf-8")
                if target:
                    target.parent.mkdir(parents=True, exist_ok=True)
                    target.write_text(new, encoding="utf-8")
                continue
            if f.suffix != ".java":
                if target and f.suffix not in (".class", ".ctxt"):
                    target.parent.mkdir(parents=True, exist_ok=True)
                    shutil.copy2(f, target)
                continue
            old = f.read_text(encoding="utf-8")
            new = migrate(old)
            todos += new.count(TODO)
            if new != old:
                changed += 1
                if not args.write and not args.out:
                    sys.stdout.writelines(difflib.unified_diff(old.splitlines(True), new.splitlines(True), str(f), str(f)))
            if args.write and new != old:
                f.write_text(new, encoding="utf-8")
            if target:
                target.parent.mkdir(parents=True, exist_ok=True)
                target.write_text(new, encoding="utf-8")
    print(f"migrate-7: {changed} files changed, {todos} TODO 7.0 notes", file=sys.stderr)


if __name__ == "__main__":
    main()
