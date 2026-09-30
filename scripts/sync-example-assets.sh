#!/bin/bash
# Copies the assets of the demos under src/examples/java/demos into
# docs/public/examples, where the online examples in docs/book/examples load
# them from. The demos are the only copy that is committed; this one is
# rebuilt from them, so the two cannot drift apart.
#
#   ./scripts/sync-example-assets.sh
#
# ./build.sh runs it. Run it yourself before `npx hyperbook dev`.
set -e

ROOT=$(cd "$(dirname "$0")/.." && pwd)
DEMOS="$ROOT/src/examples/java/demos"
OUT="$ROOT/docs/public/examples"

# The page name on the docs site, then the demo folder it is built from.
EXAMPLES="
cat cat
clock clock
donut-io donutIO
pipes pipes
robot robot
sensing sensing
shakespeare shakespeare
smart-rocket smartRocket
stress-test stressTest
ui ui
"

rm -rf "$OUT"
echo "$EXAMPLES" | while read -r page demo; do
  [ -n "$page" ] || continue
  # Only committed files, so the .class and .ctxt files BlueJ leaves next to
  # the sources are never published.
  git -C "$DEMOS/$demo" ls-files -z -- . ':!*.java' | while IFS= read -r -d '' file; do
    mkdir -p "$OUT/$page/$(dirname "$file")"
    cp "$DEMOS/$demo/$file" "$OUT/$page/$file"
  done
done
