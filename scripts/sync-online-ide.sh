#!/usr/bin/env bash
#
# Synchronizes the browser compatibility artifacts and the release catalogs into an
# online-ide checkout, then verifies that the checkout is current. Run it from the
# release branch (see RELEASING.md); the Version Bump PR asks for it.
#
#   ./scripts/sync-online-ide.sh              use the sibling ../online-ide checkout
#   ./scripts/sync-online-ide.sh /path/to/ide  use an explicit checkout
#
# Commit and push the changes in the online-ide checkout afterwards; running its
# browser tests (npm run test:browser) is the remaining manual step.
set -euo pipefail

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
online_ide="${1:-$root/../online-ide}"

if [ ! -f "$online_ide/package.json" ]; then
    echo "sync-online-ide: no online-ide checkout at $online_ide" >&2
    echo "usage: scripts/sync-online-ide.sh [path-to-online-ide]" >&2
    exit 1
fi

cd "$root"
mvn -q -DskipTests prepare-package
python3 scripts/browser-compatibility.py --online-ide "$online_ide"
python3 scripts/release-catalogs.py --online-ide "$online_ide"
python3 scripts/browser-compatibility.py --check --online-ide "$online_ide"
python3 scripts/parity-fixture.py --check --strict --online-ide "$online_ide"
python3 scripts/release-catalogs.py --check --online-ide "$online_ide"

echo "sync-online-ide: $online_ide is synchronized; commit and push the changes there."
