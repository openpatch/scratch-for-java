#!/bin/bash

pushd ..

shopt -s expand_aliases

DIR="${BASH_SOURCE%/*}"
if [[ ! -d "$DIR" ]]; then DIR="$PWD"; fi

LOCALES=("en" "de")

VERSION=$(grep -m1 '<version>' ./pom.xml | sed -E 's/.*<version>([^<]+)<\/version>.*/\1/')

for LOCALE in ${LOCALES[@]}; do
    cp ./CHANGELOG.md ./docs/$LOCALE/book/changelog.md
    folder=$DIR/docs/$LOCALE/public/reference/
    rm -rf $folder
    mkdir $folder
    pushd ./src/examples/java/reference
    find . -name "*.gif" | cpio -pdm $folder
    popd

    sed -i "s/{{VERSION}}/${VERSION}/g" ./docs/${LOCALE}/book/download.md
    sed -i "s/{{VERSION}}/${VERSION}/g" ./docs/${LOCALE}/book/index.md

    rm -rf ./docs/$LOCALE/archives
    cp -R ./docs/archives ./docs/$LOCALE/archives

    # put each template in ../templates in the archives folder
    mkdir ./docs/$LOCALE/archives
    for template in ./templates/*; do
        template_name=$(basename $template)
        cp -R $template ./docs/$LOCALE/archives/$template_name
    done

    # replace {{TEMPLATES}} in the download.md with the list of templates
    template_list=""
    for template in ./templates/*; do
        template_name=$(basename $template)
        capitalize_template_name=$(echo $template_name | sed -E 's/(^|_|-)([a-z])/\U\2/g')
        template_list+="- :archive[$capitalize_template_name]{name="$template_name"}\n"
    done
    sed -i "s/{{TEMPLATES}}/${template_list}/g" ./docs/${LOCALE}/book/download.md
done

popd

npx hyperbook build
