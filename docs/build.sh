#!/bin/bash

pushd ..

shopt -s expand_aliases

DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi

. $DIR/.env

LOCALES=("en" "de")

for LOCALE in ${LOCALES[@]}
do
    cp ./CHANGELOG.md ./docs/$LOCALE/book/changelog.md
    s4j_examples_copy_to_documentation $LOCALE

    rm -rf ./docs/$LOCALE/archives
    cp -R ./examples/archives ./docs/$LOCALE/archives
done

popd

npx hyperbook build
