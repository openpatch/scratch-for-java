#!/usr/bin/env bash
#
# Writes the reference pages (docs/book/reference) from the Javadoc of the core and the
# first-party extensions, with the doclet in examples/src/tools/java/doclets, and the HTML
# Javadoc of the same packages into target/apidocs (the documentation workflow copies that
# into the built site as /javadocs).
#
#   ./scripts/reference-docs.sh
#
# The doclet reads @example.files (a reference example under examples/src/main/java/reference),
# @scratchblock, @index-in-docs, @name-in-docs and @ignore-in-docs. Pages of methods that no
# longer exist are removed: the folder is written afresh.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

echo "reference-docs: building"
# in the reactor, never `install` (AGENTS.md)
mvn -q -DskipTests package dependency:build-classpath -pl examples -am \
  -Dmdep.includeScope=compile "-Dmdep.outputFile=$WORK/classpath"

# javadoc would resolve the modules the module-info files require; the pages only need the
# packages, so it reads a copy of the sources without them
mkdir -p "$WORK/src"
for dir in core/src/main/java extensions/*/src/main/java; do
  (cd "$dir" && find . -name '*.java' ! -name module-info.java -exec cp --parents {} "$WORK/src/" \;)
done

OUT="$WORK/reference"
# the packages of the public API, shared by both runs below
PACKAGES="org.openpatch.scratch org.openpatch.scratch.extension \
  org.openpatch.scratch.tiled org.openpatch.scratch.save org.openpatch.scratch.recorder \
  org.openpatch.scratch.drawing org.openpatch.scratch.gamepad"

echo "reference-docs: writing the pages"
javadoc -quiet -public \
  -doclet doclets.Scratch4JDoclet -docletpath examples/target/classes \
  -d "$OUT" -sourcepath "$WORK/src" -classpath "$(cat "$WORK/classpath")" \
  $PACKAGES

# the same packages with the standard doclet, as the browsable Javadoc of the site; the
# markers the reference pipeline uses are known tags so they stay out of the output
echo "reference-docs: writing the HTML Javadoc"
rm -rf target/apidocs
javadoc -quiet -public -encoding UTF-8 -charset UTF-8 \
  -tag 'scratchblock:a:Scratch Block:' \
  -tag 'index-in-docs:X' -tag 'name-in-docs:X' -tag 'ignore-in-docs:X' \
  -tag 'example.files:X' -tag 'example.folder:X' -tag 'desktop-only:X' \
  -d target/apidocs -sourcepath "$WORK/src" -classpath "$(cat "$WORK/classpath")" \
  $PACKAGES

pages=$(find "$OUT" -name '*.md.json' | wc -l)
if [ "$pages" -eq 0 ]; then
  echo "reference-docs: the doclet wrote nothing; the old pages are kept." >&2
  exit 1
fi
rm -rf docs/book/reference
cp -r "$OUT" docs/book/reference
echo "reference-docs: wrote $pages pages into docs/book/reference"
