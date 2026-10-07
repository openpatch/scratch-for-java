#!/usr/bin/env python3
"""Generate versioned release catalogs; --check never writes.

Run after mvn prepare-package and browser-compatibility.py. Consumers may use
the committed artifacts without a sibling checkout. Explicit --studio and
--online-ide paths synchronize or check their offline snapshots.
"""
import argparse
from collections import Counter
import hashlib
import json
from pathlib import Path
import re
import sys
import xml.etree.ElementTree as ET
import zipfile

ROOT = Path(__file__).resolve().parents[1]
CATALOGS = ROOT / "catalogs"
def sheets():
    registry = (ROOT / "src/main/java/org/openpatch/scratch/internal/BuiltinAssets.java").read_text()
    declaration = re.search(r'String\[\]\s+SHEETS\s*=\s*\{([^}]+)\}', registry)
    if not declaration:
        raise ValueError("Cannot discover the library's atlas lookup order")
    return re.findall(r'"([^"]+)"', declaration[1])


def encoded(value):
    return json.dumps(value, ensure_ascii=False, indent=2) + "\n"


def simple(value):
    return re.sub(r"\b(?:\w+\.)+(\w+)", r"\1", value)


def erase(value):
    while re.search(r"<[^<>]*>", value):
        value = re.sub(r"<[^<>]*>", "", value)
    return simple(value).replace(" ", "").replace("string", "String")


def identity(group, parameters):
    return group + "(" + ",".join(erase(p) for p in parameters) + ")"


def browser_descriptions(checkout):
    """Seed overrides once from existing translations, keyed by public identity."""
    folder = checkout / "src/compiler/java/runtime/graphics/scratch"
    comments = (folder / "ScratchLibraryComments.ts").read_text()
    german = {}
    for name, body in re.findall(r"static\s+(\w+)\s*=\s*\(\)\s*=>\s*lm\(\{(.*?)\}\)", comments, re.S):
        match = re.search(r'"de":\s*("(?:[^"\\]|\\.)*")', body)
        if match:
            german[name] = json.loads(match[1])
    out = {}
    for source in sorted(folder.glob("*.ts")):
        text = source.read_text()
        owner = ""
        for match in re.finditer(r'\{[^{}]*?\bsignature:\s*(["\'])(.*?)\1[^{}]*?\bcomment:\s*SRC\.(\w+)[^{}]*?\}', text, re.S):
            signature, comment = match.group(2), match.group(3)
            declaration = re.search(r"\b(?:class|enum|interface)\s+(\w+)", signature)
            if declaration:
                owner = declaration[1]
                continue
            method = re.search(r"(\w+)\s*\((.*?)\)", signature)
            if not owner or not method or comment not in german:
                continue
            parameters = []
            # Types may contain generics; commas at depth zero separate parameters.
            depth, part = 0, ""
            for char in method[2] + ",":
                depth += (char == "<") - (char == ">")
                if char == "," and depth == 0:
                    if part.strip():
                        parameters.append(part.strip().rsplit(" ", 1)[0])
                    part = ""
                else:
                    part += char
            name = "constructor" if method[1] == owner else method[1]
            out[identity(owner + "/" + name, parameters)] = {"de": {"description": german[comment]}}
    return out


def generate(translations):
    contract = json.loads((ROOT / "compatibility/contract.json").read_text())
    version = contract["desktopVersion"]
    header = {"schemaVersion": 1, "libraryVersion": version,
              "browserVersion": contract["browser"]["version"]}
    availability = {member["id"]: member for member in contract["members"]}
    methods = []
    for path in sorted((ROOT / "docs/book/reference").rglob("*.md.json")):
        page = json.loads(path.read_text())
        if "api" not in page or not page["api"]["owner"].startswith("org.openpatch.scratch."):
            continue
        group = path.relative_to(ROOT / "docs/book/reference").as_posix().removesuffix(".md.json")
        for signature in page["api"]["signatures"]:
            if "parameterNames" not in signature:
                raise ValueError("Regenerate Javadoc metadata with mvn prepare-package")
            identifier = identity(group, signature["parameters"])
            description = signature["description"] or page["description"]
            methods.append({
                "id": identifier, "memberId": group, "owner": page["api"]["owner"],
                "className": page["api"]["owner"].split(".")[-1],
                "methodName": "constructor" if signature["constructor"] else signature["name"],
                "static": signature["static"], "returnType": simple(signature["returns"]),
                "parameterTypes": signature["parameters"], "parameterNames": signature["parameterNames"],
                "params": [simple(t) + " " + n for t, n in zip(signature["parameters"], signature["parameterNames"])],
                "summary": description.split("\n\n")[0], "description": description,
                "scratchblock": signature["scratchblock"] or None,
                "paramDocs": [n + ": " + d for n, d in zip(signature["parameterNames"], signature["parameterDescriptions"])],
                "returns": signature.get("returnDescription") or None, "since": signature["since"],
                "docsUrl": "https://scratch4j.openpatch.org/reference/" + group,
                "availability": availability[group]["status"],
                "availabilityReason": availability[group]["reason"],
                "translations": translations.get(identifier, {}),
            })
    identifiers = {m["id"] for m in methods}
    if len(identifiers) != len(methods):
        raise ValueError("Duplicate API identifiers")
    stale = set(translations) - identifiers
    if stale:
        raise ValueError("Stale translation overrides: " + ", ".join(sorted(stale)))
    images = []
    for sheet in sheets():
        for element in ET.parse(ROOT / "src/main/resources/images" / (sheet + ".xml")).getroot():
            name = Path(element.get("name")).stem
            images.append({"id": sheet + "/" + name, "name": name, "sheet": sheet,
                           "sheetPath": "images/" + sheet + ".png",
                           **{key: int(element.get(key)) for key in ("x", "y", "width", "height")},
                           "direction": float(element.get("direction", "90"))})
    # Match the library registry: the first qualified entry and first bare name win.
    unique = {}
    for image in images:
        unique.setdefault(image["id"].lower(), image)
    images = list(unique.values())
    bare = set()
    for image in images:
        image["referenceName"] = image["id"] if image["name"].lower() in bare else image["name"]
        bare.add(image["name"].lower())
    sounds = [{"id": p.stem, "path": p.relative_to(ROOT / "src/main/resources").as_posix()}
              for p in sorted((ROOT / "src/main/resources/sounds").rglob("*"))
              if p.suffix.lower() in {".ogg", ".wav", ".aiff", ".au"}]
    examples = []
    for kind, folder in [("demo", ROOT / "src/examples/java/demos"),
                         ("tutorial", ROOT / "docs/archives")]:
        for project in sorted(folder.iterdir()):
            if not project.is_dir() or not list(project.glob("*.java")):
                continue
            files = [p for p in sorted(project.rglob("*")) if p.is_file()
                     and p.suffix not in {".class", ".ctxt"} and p.name != ".DS_Store"
                     and not any(part in {"+libs", ".git", "target", ".scratch4j"} for part in p.relative_to(project).parts)]
            examples.append({"id": kind + "/" + project.name, "kind": kind,
                             "source": project.relative_to(ROOT).as_posix(),
                             "libraryVersion": version, "flavour": "standard",
                             "requiredSheets": required_sheets(files, images),
                             "files": [{"path": p.relative_to(project).as_posix(),
                                        "sha256": hashlib.sha256(p.read_bytes()).hexdigest()} for p in files]})
    return {"api.json": {**header, "methods": methods},
            "assets.json": {**header, "license": "CC0", "images": images, "sounds": sounds},
            "examples.json": {**header, "examples": examples}}


def required_sheets(files, images):
    literals = []
    for file in files:
        if file.suffix == ".java":
            literals.extend(re.findall(r'"([^"\n]+)"', file.read_text()))
    required = set()
    for value in literals:
        pattern = re.escape(value).replace("%d", r"\d+")
        for image in images:
            if re.fullmatch(pattern, image["name"]) or re.fullmatch(pattern, image["id"]):
                required.add(image["sheet"])
    return sorted(required)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true")
    parser.add_argument("--online-ide", type=Path)
    parser.add_argument("--studio", type=Path)
    parser.add_argument("--seed-translations", action="store_true")
    parser.add_argument("--archive", type=Path, help="Write a versioned release artifact including ordinary example files")
    args = parser.parse_args()
    translations_path = CATALOGS / "translations.json"
    if args.seed_translations:
        if args.check or not args.online_ide:
            raise ValueError("Seeding requires --online-ide and cannot be used with --check")
        contract = json.loads((ROOT / "compatibility/contract.json").read_text())
        known = {identity(m["id"], s["parameters"]) for m in contract["members"] for s in m["signatures"]}
        translations = {key: value for key, value in browser_descriptions(args.online_ide).items() if key in known}
        CATALOGS.mkdir(exist_ok=True)
        translations_path.write_text(encoded(translations))
    translations = json.loads(translations_path.read_text())
    catalogs = generate(translations)
    outputs = {}
    lines = ["---", "name: Release catalogs", "---", "", "# Release catalogs", "",
             "Library " + catalogs["api.json"]["libraryVersion"] + "; browser " + catalogs["api.json"]["browserVersion"] + ".", "",
             "Generated release metadata is available offline inside the library JAR and Studio.", "",
             "- [API signatures, documentation and translations](/catalogs/api.json)",
             "- [Images, atlas geometry and sounds](/catalogs/assets.json)",
             "- [Examples and source checksums](/catalogs/examples.json)", "",
             "## Examples", "", "| Example | Flavor | Library |", "| --- | --- | --- |"]
    for example in catalogs["examples.json"]["examples"]:
        lines.append("| " + example["id"] + " | " + example["flavour"] + " | " + example["libraryVersion"] + " |")
    outputs[ROOT / "docs/book/catalogs.md"] = "\n".join(lines) + "\n"
    for name, content in catalogs.items():
        for folder in [CATALOGS, ROOT / "src/main/resources/catalogs", ROOT / "docs/public/catalogs"]:
            outputs[folder / name] = encoded(content)
        if args.studio:
            if not (args.studio / "core/pom.xml").is_file():
                raise ValueError("Studio checkout is missing: " + str(args.studio))
            outputs[args.studio / "core/src/main/resources/catalogs" / name] = encoded(content)
        if args.online_ide:
            if not (args.online_ide / "package.json").is_file():
                raise ValueError("Browser checkout is missing: " + str(args.online_ide))
            outputs[args.online_ide / "src/compiler/java/runtime/graphics/scratch/catalogs" / name] = encoded(content)
            outputs[args.online_ide / "public/catalogs" / name] = encoded(content)
    checks = (ROOT / "compatibility/checks/PortableBehaviorChecks.java").read_text()
    outputs[ROOT / "src/test/java/portable/PortableBehaviorChecks.java"] = "package portable;\n\n" + checks
    if args.online_ide:
        outputs[args.online_ide / "src/test/browser/PortableBehaviorChecks.java"] = checks
    binary_outputs = {}
    for sheet in sheets():
        source = ROOT / "src/main/resources/images" / (sheet + ".png")
        if args.online_ide:
            binary_outputs[args.online_ide / "assets/graphics/scratch" / source.name] = source.read_bytes()
        if args.studio:
            binary_outputs[args.studio / "core/src/main/resources/images" / source.name] = source.read_bytes()
    stale = []
    for path, content in binary_outputs.items():
        if args.check:
            if not path.exists() or path.read_bytes() != content:
                stale.append(str(path))
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(content)
    for path, content in outputs.items():
        if args.check:
            if not path.exists() or path.read_text() != content:
                stale.append(str(path))
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(content)
    if stale:
        raise ValueError("Stale catalogs:\n" + "\n".join(stale))
    if args.archive:
        if args.check:
            raise ValueError("--check cannot write an archive")
        args.archive.parent.mkdir(parents=True, exist_ok=True)
        with zipfile.ZipFile(args.archive, "w", zipfile.ZIP_DEFLATED) as archive:
            for name, value in catalogs.items():
                archive.writestr("catalogs/" + name, encoded(value))
            for resource in sorted((ROOT / "src/main/resources/images").iterdir()):
                if resource.suffix in {".png", ".xml", ".txt"}:
                    archive.write(resource, resource.relative_to(ROOT / "src/main/resources").as_posix())
            for example in catalogs["examples.json"]["examples"]:
                for entry in example["files"]:
                    file = ROOT / example["source"] / entry["path"]
                    archive.writestr("examples/" + example["id"] + "/" + entry["path"], file.read_bytes())
    print("release-catalogs:", len(catalogs["api.json"]["methods"]), "API overloads,",
          len(catalogs["assets.json"]["images"]), "images,", len(catalogs["assets.json"]["sounds"]),
          "sounds,", len(catalogs["examples.json"]["examples"]), "examples; checked" if args.check else "examples; generated")


if __name__ == "__main__":
    try:
        main()
    except (OSError, ValueError, KeyError) as error:
        sys.exit("release-catalogs: " + str(error))
