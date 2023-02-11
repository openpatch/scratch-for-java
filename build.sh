#!/bin/bash

shopt -s expand_aliases

DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi

. $DIR/.env

cp CHANGELOG.md ./docs/en/book/changelog.md
s4j_examples_copy_to_documentation
npx hyperbook build
