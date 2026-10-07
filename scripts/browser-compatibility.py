#!/usr/bin/env python3
"""Generate the browser contract, reference fixtures and documentation from Javadoc.

Run mvn -DskipTests prepare-package first. --online-ide accepts any checkout path;
without it, documentation builds use the committed browser declaration snapshot.
--check is read-only and fails on missing inputs or stale generated artifacts.
"""
import argparse
import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
COMPAT = ROOT / "compatibility"
REFERENCE = ROOT / "docs/book/reference"
STATES = {"implemented", "desktop-only", "awaiting-browser-support"}


def load(file):
    return json.loads(file.read_text(encoding="utf-8"))


def encoded(value):
    return json.dumps(value, indent=2, ensure_ascii=False) + "\n"


def simple_type(value):
    # Erased parameter types identify overloads. Keep array dimensions and
    # primitive types; generic bounds and return types remain in the contract.
    out, depth = "", 0
    for char in value:
        if char == "<":
            depth += 1
        elif char == ">":
            depth -= 1
        elif not depth:
            out += char
    out = re.sub(r"\b(?:\w+\.)+(\w+)", r"\1", out)
    return out.replace(" ", "").replace("...", "[]").replace("string", "String")


def signature_key(signature):
    return (signature["name"], signature["constructor"], signature["static"],
            tuple(simple_type(p) for p in signature["parameters"]))


def generate(policy, inventory, accepted):
    members, examples, annotated = [], [], {}
    seen_overrides, seen_accepted = set(), set()
    for file in sorted(REFERENCE.rglob("*.md.json")):
        page = load(file)
        if page.get("template") not in {"class-method", "class-constructor"}:
            continue
        if "api" not in page:
            raise ValueError(f"{file}: API metadata missing; regenerate with mvn prepare-package")
        member_id = file.relative_to(REFERENCE).as_posix().removesuffix(".md.json")
        api = page["api"]
        if not api["owner"].startswith("org.openpatch.scratch."):
            continue
        declared = inventory["standard"].get(api["owner"], [])
        declared_keys = {signature_key(signature) for signature in declared}
        missing = [signature for signature in api["signatures"] if signature_key(signature) not in declared_keys]
        if page.get("desktopOnly"):
            status, reason = "desktop-only", "This API requires the desktop runtime. Browser declarations may report the limitation without performing the operation."
        elif missing or api["owner"] not in inventory["standard"]:
            status, reason = "awaiting-browser-support", "Some desktop overloads are not declared by the browser. See the missing signatures in the compatibility contract."
        else:
            status, reason = "implemented", "Declared by the browser; behavioral limitations and compiler dialect differences still apply."
        if member_id in policy["overrides"]:
            override = policy["overrides"][member_id]
            status, reason = override["status"], override["reason"]
            seen_overrides.add(member_id)
        if status not in STATES or not reason:
            raise ValueError(f"Invalid availability for {member_id}")
        member = {"id": member_id, **api, "status": status, "reason": reason,
                  "missingBrowserSignatures": missing}
        members.append(member)
        page["browser"] = {"status": status, "reason": reason, "unavailable": status != "implemented"}
        for index, example in enumerate(page.get("examples", [])):
            if not example.get("online"):
                continue
            example_id = f"{member_id}#{index}"
            expected = accepted.get(example_id, [])
            if expected:
                if member_id not in policy["overrides"] or status != "awaiting-browser-support":
                    raise ValueError(f"{example_id}: expected errors need an explicit awaiting-support policy")
                seen_accepted.add(example_id)
            files = [{"name": "Example.java", "source": example["online"]}]
            for name in example.get("onlineFiles", []):
                if name.endswith(".java"):
                    source = ROOT / "docs/public/reference" / name
                    files.append({"name": name, "source": source.read_text(encoding="utf-8")})
            example["browserFiles"] = files[1:]
            # A compiling example can still demonstrate unimplemented behavior,
            # as clone() does. Keep it readable, without an interactive runner.
            runnable = status != "desktop-only" and not expected and (
                status == "implemented" or member_id not in policy["overrides"])
            example["browserRunnable"] = runnable
            examples.append({"id": example_id, "member": member_id, "runnable": runnable,
                             "files": files, "expectedDiagnostics": expected})
        annotated[file] = encoded(page)
    if not members or not examples:
        raise ValueError("Generated reference is missing or empty; run mvn prepare-package")
    if seen_overrides != set(policy["overrides"]):
        raise ValueError(f"Stale availability overrides: {set(policy['overrides']) - seen_overrides}")
    if seen_accepted != set(accepted):
        raise ValueError(f"Stale diagnostic exceptions: {set(accepted) - seen_accepted}")
    contract = {
        "schemaVersion": policy["schemaVersion"], "desktopVersion": policy["desktopVersion"],
        "browser": {"version": policy["browserVersion"], "flavors": policy["flavors"]},
        "scope": "Public constructors and method groups documented by the Javadoc doclet. Overload matching uses simple erased parameter names, constructor kind and static/instance kind; qualified parameter and return types are recorded. This is a source compatibility inventory, not a binary compatibility guarantee.",
        "limitations": policy["limitations"],
        "referenceValidation": {"flavor": "standard", "examples": len(examples),
                                "knownUnsupportedExamples": len(accepted)},
        "members": members,
    }
    return contract, examples, annotated


def table(contract):
    lines = ["---", "name: Browser compatibility", "---", "", "# Browser compatibility", "",
             f"Desktop library: **{contract['desktopVersion']}**. Browser implementation: **{contract['browser']['version']}**.", "",
             "Generated by `scripts/browser-compatibility.py`. [Download the machine-readable contract](/compatibility.json).", "",
             contract["scope"], "", "## Library flavors", "",
             "| Flavor | Embedded libraries | List type |", "| --- | --- | --- |"]
    for name, flavor in contract["browser"]["flavors"].items():
        lines.append(f"| {name} | `{', '.join(flavor['libraries'])}` | `{flavor['listType']}` |")
    lines += ["", "## Limits of this revision", ""]
    lines += [f"- {limit}" for limit in contract["limitations"]]
    lines += ["", "## Documented API", "",
              "An implemented entry has its documented overloads declared in the browser. The reference compilation gate checks the code examples; rendering and timing need separate runtime tests.", "",
              "| API group | Browser availability | Details |", "| --- | --- | --- |"]
    for member in contract["members"]:
        lines.append(f"| [{member['id']}](/reference/{member['id']}) | {member['status']} | {member['reason']} |")
    return "\n".join(lines) + "\n"


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--online-ide", type=Path, help="explicit browser checkout to synchronize or check")
    parser.add_argument("--check", action="store_true", help="fail on stale generated artifacts")
    args = parser.parse_args()
    policy = load(COMPAT / "browser-policy.json")
    version = ET.parse(ROOT / "pom.xml").findtext("{http://maven.apache.org/POM/4.0.0}version")
    if policy["desktopVersion"] != version:
        raise ValueError("Desktop version changed; review the browser policy/version before regenerating")
    inventory_file = COMPAT / "browser-api.json"
    inventory = load(args.online_ide / "public/scratch-api.json" if args.online_ide else inventory_file)
    if set(inventory) != set(policy["flavors"]):
        raise ValueError("Browser inventory does not cover the declared library flavors")
    accepted = load(COMPAT / "browser-reference-diagnostics.json")
    contract, examples, annotated = generate(policy, inventory, accepted)
    outputs = {
        inventory_file: encoded(inventory),
        COMPAT / "contract.json": encoded(contract),
        ROOT / "docs/public/compatibility.json": encoded(contract),
        ROOT / "docs/book/compatibility.md": table(contract),
    }
    if args.online_ide:
        outputs[args.online_ide / "public/scratch-compatibility.json"] = encoded(contract)
        outputs[args.online_ide / "src/compiler/java/runtime/graphics/scratch/ScratchCompatibility.ts"] = (
            "// GENERATED by scratch-for-java/scripts/browser-compatibility.py.\n"
            + "export const scratchBrowserVersion = " + json.dumps(policy["browserVersion"]) + ";\n"
            + "export const scratchDesktopVersion = " + json.dumps(version) + ";\n"
            + "export const scratchBrowserMembers = " + encoded([
                {key: member[key] for key in ("id", "status", "reason")} for member in contract["members"]
            ]).rstrip() + ";\n")
        for name in ("LifecycleProbe.java", "lifecycle-expected.txt"):
            outputs[args.online_ide / "src/test/browser" / name] = (COMPAT / "probes" / name).read_text(encoding="utf-8")
        outputs[args.online_ide / "src/test/fixtures/scratch-reference-examples.json"] = encoded({
            "desktopVersion": version, "browserVersion": policy["browserVersion"], "examples": examples})
    if not args.check:
        outputs.update(annotated)
    stale = []
    for file, content in outputs.items():
        if args.check:
            if not file.exists() or file.read_text(encoding="utf-8") != content:
                stale.append(str(file))
        else:
            file.parent.mkdir(parents=True, exist_ok=True)
            file.write_text(content, encoding="utf-8")
    if stale:
        raise ValueError("Generated artifacts are missing or stale:\n" + "\n".join(stale))
    print(f"browser-compatibility: {len(contract['members'])} API groups, {len(examples)} examples, "
          f"{len(accepted)} explicit unsupported examples; {'checked' if args.check else 'generated'}.")


if __name__ == "__main__":
    try:
        main()
    except (OSError, ValueError, KeyError) as error:
        sys.exit(f"browser-compatibility: {error}")
