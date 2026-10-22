#!/usr/bin/env bash
#
# Checks the values the desktop and the browser both have to produce (core's ParityTest, headless
# since 7.0 - no window needed any more, so it also runs in `mvn test`).
#
#   ./scripts/parity.sh            check against core/src/test/resources/parity/expected.txt
#   ./scripts/parity.sh --record   write that file from what the library answers now
#
# After recording, python3 scripts/parity-fixture.py carries the window-free values to the
# online IDE's test suite.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

record=()
[ "${1:-}" = "--record" ] && record=(-Dparity.record=true)

mvn -q test -pl core -Dtest=ParityTest -Dsurefire.failIfNoSpecifiedTests=false "${record[@]}"
if [ ${#record[@]} -gt 0 ]; then
  echo "parity: recorded $(wc -l < core/src/test/resources/parity/expected.txt) values; read the diff, then"
  echo "        python3 scripts/parity-fixture.py so the browser is held to them too."
else
  echo "parity: all $(wc -l < core/src/test/resources/parity/expected.txt) values match."
fi
