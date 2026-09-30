#!/bin/bash
# Fills the interactive examples on the pages under docs/book/examples from the
# demos under src/examples/java/demos, and copies the demos' assets to
# docs/public/examples where those pages load them from.
#
#   ./scripts/update-demo-pages.sh
#
# Run it after changing a demo, and commit the pages it touched. DemoPagesTest
# fails while a page still shows an older version of its demo.
set -e

cd "$(dirname "$0")/.."
mvn -q compile exec:java -Dexec.mainClass=docs.GenerateDemoPages -Dexec.args=--write
