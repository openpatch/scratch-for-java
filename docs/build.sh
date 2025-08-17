#!/bin/bash

pushd ..

shopt -s expand_aliases

DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi

. $DIR/.env

LOCALES=("en" "de")

VERSION=$(grep -m1 '<version>' ./pom.xml | sed -E 's/.*<version>([^<]+)<\/version>.*/\1/')

for LOCALE in ${LOCALES[@]}; do
    cp ./CHANGELOG.md ./docs/$LOCALE/book/changelog.md
    s4j_examples_copy_to_documentation $LOCALE
    folder=./docs/$LOCALE/public/reference/
    rm -rf $folder
    mkdir $folder
    pushd ./src/examples/java/reference
    find . -name "*.gif" | cpio -pdm $folder
    popd

    sed -i "s/{{VERSION}}/${VERSION}/g" ./docs/${LOCALE}/book/download.md
    sed -i "s/{{VERSION}}/${VERSION}/g" ./docs/${LOCALE}/book/index.md

    rm -rf ./docs/$LOCALE/archives
    cp -R ./docs/archives ./docs/$LOCALE/archives
done

popd

npx hyperbook build
