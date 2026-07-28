#!/usr/bin/env bash
#
# Renders the scenes in VisualProbe and checks they still look the way they did.
# See src/examples/java/parity/VisualProbe.java for what is being drawn and why.
#
#   ./scripts/visual-parity.sh            check against the recorded frames
#   ./scripts/visual-parity.sh --record   record them from what is drawn now
#
# The desktop renderer is deterministic under Xvfb - two runs are bit-identical -
# so this compares exactly rather than with a tolerance, and any difference at
# all is a real change in what is drawn. The comparing is done by
# src/test/java/parity/VisualParity.java, which boxes whatever moved.
#
# This is the desktop against itself. The browser draws with another renderer and
# another font engine, so the two cannot be compared this way; ParityProbe and
# scripts/parity.sh cover what the two can both be asked in numbers.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

EXPECTED="src/test/resources/parity/frames"
ACTUAL="$(mktemp -d)"
DIFFS="$ROOT/target/visual-parity"
CLASSPATH_FILE="$(mktemp)"
trap 'rm -rf "$ACTUAL" "$CLASSPATH_FILE"' EXIT

record=false
[ "${1:-}" = "--record" ] && record=true

if ! command -v xvfb-run >/dev/null; then
  echo "visual-parity: xvfb-run is needed to draw without a screen (package xvfb)." >&2
  exit 1
fi

echo "visual-parity: compiling"
# test-compile, not compile: the comparing half and the library it uses are
# test scope, so that nothing using this library ends up with them
mvn -q -DskipTests test-compile
mvn -q dependency:build-classpath -Dmdep.includeScope=test "-Dmdep.outputFile=$CLASSPATH_FILE"

echo "visual-parity: drawing the scenes"
xvfb-run -a java -cp "target/classes:$(cat "$CLASSPATH_FILE")" parity.VisualProbe "$ACTUAL" 2>/dev/null

count=$(find "$ACTUAL" -name '*.png' | wc -l)
if [ "$count" -eq 0 ]; then
  echo "visual-parity: nothing was drawn - the probe probably failed to start." >&2
  exit 1
fi

if $record; then
  mkdir -p "$EXPECTED"
  rm -f "$EXPECTED"/*.png
  cp "$ACTUAL"/*.png "$EXPECTED"/
  echo "visual-parity: recorded $count scenes into $EXPECTED"
  exit 0
fi

if [ ! -d "$EXPECTED" ]; then
  echo "visual-parity: no $EXPECTED yet - run ./scripts/visual-parity.sh --record first." >&2
  exit 1
fi

# Whatever failed last time is not what failed this time. Left lying around, an
# old picture reads as a current one.
rm -rf "$DIFFS"

if java -cp "target/test-classes:target/classes:$(cat "$CLASSPATH_FILE")" \
    parity.VisualParity "$EXPECTED" "$ACTUAL" "$DIFFS"; then
  exit 0
fi

# The frames as this machine drew them, kept next to the differences. Two runs on
# one machine are bit-identical, but two machines only agree if they rasterise
# the same way - so where the change turns out to be the machine rather than the
# library, these are the ones to record.
mkdir -p "$DIFFS/as-drawn"
cp "$ACTUAL"/*.png "$DIFFS/as-drawn/"
echo "        - target/visual-parity/as-drawn/ has them as this machine drew them"
exit 1
