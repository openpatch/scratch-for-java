#!/bin/bash
# Fills the interactive examples on the pages under docs/book/examples from the
# demos under examples/src/main/java/demos, and copies the demos' assets to
# docs/public/examples where those pages load them from.
#
#   ./scripts/update-demo-pages.sh
#
# Run it after changing a demo, and commit the pages it touched. DemoPagesTest
# fails while a page still shows an older version of its demo.
set -e

cd "$(dirname "$0")/.."
# in the reactor, never against ~/.m2 (AGENTS.md): -am hands out core and the
# extensions as their target/classes
mvn -q compile dependency:build-classpath -pl examples -am \
  -Dmdep.outputFile='${project.build.directory}/run-classpath.txt' -Dmdep.includeScope=runtime
java -cp "examples/target/classes:$(cat examples/target/run-classpath.txt)" docs.GenerateDemoPages --write
