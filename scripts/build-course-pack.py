#!/usr/bin/env python3
"""Build ordinary project starters/checkpoints and offline teaching material.

--curriculum adds the selected Spielwerkstatt checkpoints from an explicit
checkout. The basic seven-tutorial pack needs only this library repository.
"""
import argparse
import base64
import importlib.util
import json
from pathlib import Path
import re
import sys
import zipfile

ROOT = Path(__file__).resolve().parents[1]
LESSONS = [
    ("getting-started", "Stage, Sprite, costume", "Add a rabbit and explain why this.add is needed."),
    ("make-it-walk", "Frames, costumes, timers", "Animate movement without blocking the next frame."),
    ("catch-the-coins", "Score, randomness, collisions", "Catch coins and test when the score changes."),
    ("red-light-green-light", "Messages and shared state", "Coordinate sprites through a broadcast and explain its order."),
    ("guess-the-number", "Input, conditions, feedback", "Read an answer and give useful feedback for several guesses."),
    ("bouncy-hedgehog", "A game with imported assets", "Build and test the hedgehog bounce using the supplied project."),
    ("dodge-the-rocks", "Stages and game over", "Switch stages when a collision ends the game."),
]
SELECTED = ["spielwerkstatt", "spielwerkstatt-ef-02-variablen",
            "spielwerkstatt-ef-05-felder", "spielwerkstatt-q-07-testen"]


def start_class(files):
    candidates = []
    for name, source in files.items():
        if not name.endswith(".java"):
            continue
        source = source.decode("utf-8")
        for kind in ("Window", "Stage"):
            match = re.search(r"\bclass\s+(\w+)\s+extends\s+" + kind + r"\b", source)
            if match:
                candidates.append((0 if kind == "Window" else 1, match[1]))
        if re.search(r"\bvoid\s+main\s*\(", source):
            candidates.append((2, Path(name).stem))
    return min(candidates)[1] if candidates else ""


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--curriculum", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    version = json.loads((ROOT / "catalogs/examples.json").read_text())["libraryVersion"]
    projects, files = [], {}

    def add(identifier, title, sources, lesson, checkpoint, flavour="standard", metadata=None):
        path = "projects/" + identifier
        settings = {"version": 1, "portableVersion": 1, "libraryVersion": version,
                    "flavour": flavour, "startStage": start_class(sources), "lesson": lesson,
                    "checkpoint": checkpoint, "sourceEnvironment": "studio",
                    "browserFeatures": [], "externalDependencies": [], **(metadata or {}), "libraryVersion": version}
        for name, content in sources.items():
            files[path + "/" + name] = content
        files[path + "/.scratch4j/project.json"] = (json.dumps(settings, ensure_ascii=False, indent=2) + "\n").encode()
        test_classes = [Path(name).stem for name, source in sources.items()
                        if name.endswith(".java") and re.search(rb"@Test\b", source)]
        if test_classes:
            files[path + "/.scratch4j/checks.json"] = (json.dumps({
                "schemaVersion": 1, "mode": "logic", "classes": test_classes}, indent=2) + "\n").encode()
        projects.append({"path": path, "title": title, "lesson": lesson, "checkpoint": checkpoint, "flavour": flavour})

    previous = {"MyStage.java": b'import org.openpatch.scratch.*;\npublic class MyStage extends Stage {\n'
                b'  public MyStage() { super(480, 360); }\n'
                b'  // TODO: create a Sprite, add its bunny1_stand costume, then add it to this stage.\n'
                b'  public static void main(String[] args) { new MyStage(); }\n}\n'}
    for index, (lesson, concepts, task) in enumerate(LESSONS, 1):
        folder = ROOT / "docs/archives" / (lesson + "-100")
        checkpoint = {p.relative_to(folder).as_posix(): p.read_bytes() for p in sorted(folder.rglob("*"))
                      if p.is_file() and "+libs" not in p.relative_to(folder).parts and p.suffix not in {".class", ".ctxt"}}
        starter = dict(previous)
        if lesson == "bouncy-hedgehog":
            folder = ROOT / "docs/archives/bouncy-hedgehog"
            starter = {p.relative_to(folder).as_posix(): p.read_bytes() for p in sorted(folder.rglob("*"))
                       if p.is_file() and "+libs" not in p.relative_to(folder).parts and p.suffix not in {".class", ".ctxt"}}
        task_text = ("# " + str(index) + ". " + lesson + "\n\nConcepts: " + concepts + "\n\n" + task
                     + "\n\nStart from the previous project and adapt it, or compare the supplied checkpoint."
                     + "\n\nReflection: What changed? What evidence shows it works? Why did you choose this approach?"
                     + "\n\nSave a ZIP, reopen it in the other environment, and record one compatibility observation.\n")
        starter["TASK.md"] = task_text.encode()
        add(f"{index:02}-{lesson}-starter", lesson + " — starter", starter, lesson, "starter")
        checkpoint["TASK.md"] = task_text.encode()
        add(f"{index:02}-{lesson}-checkpoint", lesson + " — checkpoint", checkpoint, lesson, "complete")
        previous = checkpoint
        files["lessons/" + lesson + ".md"] = (ROOT / "docs/book/tutorials" / (lesson + ".md")).read_bytes()

    if args.curriculum:
        generator = args.curriculum / "tools/spielwerkstatt/erzeuge_checkpoints.py"
        spec = importlib.util.spec_from_file_location("course_checkpoints", generator)
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        for identifier in SELECTED:
            workspace = module.workspace(identifier, module.CHECKPOINTS[identifier])
            sources = {}
            for entry in workspace["modules"]:
                text = entry["text"]
                sources[entry["name"]] = base64.b64decode(text.split(",", 1)[1]) if text.startswith("data:") else text.encode()
            sources["TASK.md"] = ("# Spielwerkstatt\n\nChoose one mechanic from the chapter or invent your own."
                "\n\nKeep the Entwicklertagebuch: What changed? How did you test it? Why this approach?"
                "\n\nThe original reflection questions are retained in lessons/spielwerkstatt.\n").encode()
            metadata = workspace["settings"]["scratchProject"]
            metadata = {**metadata, "sourceEnvironment": "studio"}
            add(identifier, module.CHECKPOINTS[identifier], sources, "spielwerkstatt", identifier, "nrw", metadata)
        for page in sorted((args.curriculum / "book/projekte/spielwerkstatt").glob("*.md*")):
            files["lessons/spielwerkstatt/" + page.name] = page.read_bytes()

    manifest = {"schemaVersion": 1, "id": "scratch-to-java", "libraryVersion": version,
                "javaLibraryMinimum": 17, "javaStudio": 25, "projects": projects}
    files[".scratch4j/course.json"] = (json.dumps(manifest, ensure_ascii=False, indent=2) + "\n").encode()
    for name in ("teacher-notes.md", "assessment.md", "pilot.md"):
        files[name] = (ROOT / "course" / name).read_bytes()
    files["README.md"] = (b"# Scratch to Java course pack\n\n"
        b"Open this ZIP using Studio's Import course pack command. Projects are ordinary Java folders.\n"
        b"Studio copies its bundled standard/NRW library into each project; no downloads are needed.\n"
        b"Other Java IDEs need Scratch for Java " + version.encode() + b".\n\n"
        b"Starters begin with the previous lesson's project; checkpoints show a complete solution.\n"
        b"Read TASK.md, teacher-notes.md and assessment.md. Keep the original reflection activities.\n"
        b"Asset license notices are retained in the projects' README files; respect their conditions.\n"
        b"Pilot results have not been collected. Use pilot.md to record actual classroom observations.\n")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(args.output, "w", zipfile.ZIP_DEFLATED) as archive:
        for name, content in sorted(files.items()):
            entry = zipfile.ZipInfo(name, (1980, 1, 1, 0, 0, 0))
            entry.compress_type = zipfile.ZIP_DEFLATED
            archive.writestr(entry, content)
    print(f"Course pack: {len(projects)} projects / {len(files)} files -> {args.output}")


if __name__ == "__main__":
    main()
