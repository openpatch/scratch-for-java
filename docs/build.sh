#!/bin/bash

pushd ..

shopt -s expand_aliases

DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi

VERSION=$(grep -m1 '<version>' ./pom.xml | sed -E 's/.*<version>([^<]+)<\/version>.*/\1/')

cp ./CHANGELOG.md ./docs/book/changelog.md
folder=$DIR/docs/public/reference/
rm -rf $folder
mkdir $folder
pushd ./src/examples/java/reference
find . -name "*.gif" | cpio -pdm $folder
popd

mvn javadoc:javadoc@generate-json-docs

sed -i "s/{{VERSION}}/${VERSION}/g" ./docs/book/download.md
sed -i "s/{{VERSION}}/${VERSION}/g" ./docs/book/index.md
popd

npx hyperbook build
