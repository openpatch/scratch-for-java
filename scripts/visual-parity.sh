#!/usr/bin/env bash
#
# Renders the scenes in VisualProbe and checks they still look the way they did.
# See examples/src/main/java/parity/VisualProbe.java for what is drawn and why.
#
#   ./scripts/visual-parity.sh            check against the recorded frames
#   ./scripts/visual-parity.sh --record   record them from what is drawn now
#
# The renderer is deterministic under Xvfb (Mesa's llvmpipe) - two runs are bit-identical - so
# this compares exactly, and any difference at all is a real change in what is drawn. The
# comparing is done by examples/src/test/java/parity/VisualParity.java, which boxes what moved.
#
# This is the desktop against itself. The browser draws with another renderer and font engine;
# core's ParityTest covers what the two can both be asked in numbers.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

EXPECTED="examples/src/test/resources/parity/frames"
ACTUAL="$(mktemp -d)"
DIFFS="$ROOT/examples/target/visual-parity"
CLASSPATH_FILE="$(mktemp)"
trap 'rm -rf "$ACTUAL" "$CLASSPATH_FILE"' EXIT

record=false
[ "${1:-}" = "--record" ] && record=true

if ! command -v xvfb-run >/dev/null; then
  echo "visual-parity: xvfb-run is needed to draw without a screen (package xvfb)." >&2
  exit 1
fi

echo "visual-parity: building"
# in the reactor, never `install`: the version is still the released one (AGENTS.md)
mvn -q -DskipTests package dependency:build-classpath -pl all,examples -am \
  -Dmdep.includeScope=test "-Dmdep.outputFile=$CLASSPATH_FILE"
FAT_JAR="$(ls all/target/scratch-*-all.jar | grep -v nrw | head -1)"

echo "visual-parity: drawing the scenes"
# GLFW picks Wayland whenever it can; under Xvfb it has to use X11
env -u WAYLAND_DISPLAY XDG_SESSION_TYPE=x11 xvfb-run -a -s '-screen 0 480x360x24' \
  java --enable-native-access=ALL-UNNAMED -Dscratch4j.platform=x11 \
  -cp "examples/target/classes:$FAT_JAR" parity.VisualProbe "$ACTUAL" 2>/dev/null

count=$(find "$ACTUAL" -name '*.png' | wc -l)
if [ "$count" -eq 0 ]; then
  echo "visual-parity: nothing was drawn - the probe probably failed to start." >&2
  exit 1
fi

if $record; then
  mkdir -p "$EXPECTED"
  find "$EXPECTED" -maxdepth 1 -name '*.png' -delete
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

if java -cp "examples/target/test-classes:$(cat "$CLASSPATH_FILE")" \
    parity.VisualParity "$EXPECTED" "$ACTUAL" "$DIFFS"; then
  exit 0
fi

# The frames as this machine drew them, kept next to the differences. Two runs on one machine
# are bit-identical, but two machines only agree if they rasterise the same way - so where the
# change turns out to be the machine rather than the library, these are the ones to record.
mkdir -p "$DIFFS/as-drawn"
cp "$ACTUAL"/*.png "$DIFFS/as-drawn/"
echo "        - examples/target/visual-parity/as-drawn/ has them as this machine drew them"
exit 1
