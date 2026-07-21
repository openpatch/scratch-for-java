#!/bin/bash
set -e

ROOT=$(cd "$(dirname "$0")" && pwd)

VERSION=$(grep -m1 '<version>' ./pom.xml | sed -E 's/.*<version>([^<]+)<\/version>.*/\1/')

cp ./CHANGELOG.md ./docs/book/changelog.md
folder=$PWD/docs/public/reference/
rm -rf $folder
mkdir $folder
pushd ./src/examples/java/reference
find . -name "*.gif" | cpio -pdm $folder
popd

# The version is written into these pages for the build and taken out again
# afterwards. Substituting in place without restoring would burn one version
# number into the sources, and every later release would ship the wrong one.
VERSIONED_PAGES="$ROOT/docs/book/download.md $ROOT/docs/book/index.md $ROOT/docs/book/setup.md"
restore_versioned_pages() {
  for page in $VERSIONED_PAGES; do
    if [ -f "$page.orig" ]; then
      mv "$page.orig" "$page"
    fi
  done
  return 0
}
trap restore_versioned_pages EXIT

for page in $VERSIONED_PAGES; do
  cp "$page" "$page.orig"
  sed -i "s/{{VERSION}}/${VERSION}/g" "$page"
done

# Regenerates everything the docs are built from: the reference pages written by
# the doclet, and the built-in sprite and sound pages. Running only `compile`
# here would leave the reference pages as they were after the last `mvn package`,
# which is how pages for deleted methods used to survive.
mvn -q -DskipTests prepare-package

cd docs
npx hyperbook build
