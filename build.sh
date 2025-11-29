#!/bin/bash

VERSION=$(grep -m1 '<version>' ./pom.xml | sed -E 's/.*<version>([^<]+)<\/version>.*/\1/')

cp ./CHANGELOG.md ./docs/book/changelog.md
folder=$PWD/docs/public/reference/
rm -rf $folder
mkdir $folder
pushd ./src/examples/java/reference
find . -name "*.gif" | cpio -pdm $folder
popd

sed -i "s/{{VERSION}}/${VERSION}/g" ./docs/book/download.md
sed -i "s/{{VERSION}}/${VERSION}/g" ./docs/book/index.md

npx hyperbook build
