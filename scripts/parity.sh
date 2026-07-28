#!/usr/bin/env bash
#
# Runs ParityProbe on the desktop and checks it against the values the browser
# has to produce too. See src/examples/java/parity/ParityProbe.java for what is
# being compared and why.
#
#   ./scripts/parity.sh            check against src/test/resources/parity/expected.txt
#   ./scripts/parity.sh --record   write that file from what the desktop prints
#
# A window is needed even though nothing is looked at, because a costume cannot
# be loaded without one, so this runs under Xvfb rather than in `mvn test`.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

EXPECTED="src/test/resources/parity/expected.txt"
ACTUAL="$(mktemp)"
CLASSPATH_FILE="$(mktemp)"
trap 'rm -f "$ACTUAL" "$CLASSPATH_FILE"' EXIT

record=false
[ "${1:-}" = "--record" ] && record=true

if ! command -v xvfb-run >/dev/null; then
  echo "parity: xvfb-run is needed to run a window headlessly (package xvfb)." >&2
  exit 1
fi

echo "parity: compiling"
mvn -q -DskipTests compile
mvn -q dependency:build-classpath "-Dmdep.outputFile=$CLASSPATH_FILE"

echo "parity: running the probe"
# The probe prints key=value and nothing else; Processing and JOGL are chatty on
# stderr under Xvfb, and a GL warning is not a parity failure.
xvfb-run -a java -cp "target/classes:$(cat "$CLASSPATH_FILE")" parity.ParityProbe 2>/dev/null \
  | grep -E '^[a-zA-Z][a-zA-Z0-9.]*=' > "$ACTUAL"

if [ ! -s "$ACTUAL" ]; then
  echo "parity: the probe printed nothing - it probably failed to start." >&2
  exit 1
fi

if $record; then
  mkdir -p "$(dirname "$EXPECTED")"
  cp "$ACTUAL" "$EXPECTED"
  echo "parity: recorded $(wc -l < "$EXPECTED") values into $EXPECTED"
  echo "parity: now run python3 scripts/parity-fixture.py to carry them to the online IDE."
  exit 0
fi

if [ ! -f "$EXPECTED" ]; then
  echo "parity: no $EXPECTED yet - run ./scripts/parity.sh --record first." >&2
  exit 1
fi

if diff -u "$EXPECTED" "$ACTUAL" > /dev/null; then
  echo "parity: all $(wc -l < "$ACTUAL") values match."
else
  echo "parity: the desktop no longer prints what it used to."
  echo "        - if the change is wrong, fix it"
  echo "        - if it is right, run ./scripts/parity.sh --record, then"
  echo "          python3 scripts/parity-fixture.py so the browser is held to it too"
  echo
  diff -u "$EXPECTED" "$ACTUAL" || true
  exit 1
fi
