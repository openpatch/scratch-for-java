#!/bin/bash
# Puts the locally built library into every project under docs/archives, so the
# projects can be opened and run straight from a checkout.
#
#   mvn package -Pall -DskipTests   # build the jar first
#   ./scripts/link-jar.sh
#
# Nothing this creates is committed, and ./build.sh clears it again before the
# archives are zipped, so a student's download never carries a 19 MB jar.
set -e

ROOT=$(cd "$(dirname "$0")/.." && pwd)
JAR=$(ls -1 "$ROOT"/target/scratch-*-all.jar 2>/dev/null | grep -v sources | grep -v javadoc | head -1)

if [ -z "$JAR" ]; then
  echo "No jar found in target/."
  echo "Build one first:  mvn package -Pall -DskipTests"
  exit 1
fi

count=0
for libs in "$ROOT"/docs/archives/*/+libs; do
  [ -d "$libs" ] || continue
  rm -f "$libs"/scratch-*.jar
  if ln -s "$JAR" "$libs/$(basename "$JAR")" 2>/dev/null; then
    :                       # a link costs nothing and is not followed when zipping
  else
    cp "$JAR" "$libs/"      # Windows, or any filesystem without symlinks
  fi
  count=$((count + 1))
done

echo "Linked $(basename "$JAR") into $count archive projects."
echo "Open any folder under docs/archives/ with BlueJ or VS Code, or run:"
echo "    cd docs/archives/make-it-walk-100 && javac -cp '+libs/*' -d . *.java && java -cp '+libs/*:.' WalkStage"
