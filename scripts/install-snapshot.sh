#!/usr/bin/env bash
#
# Installs this branch's library into the local Maven repository as 7.0.0-SNAPSHOT, for projects
# that build against it before the release (Studio's v7 branch: scratch.version 7.0.0-SNAPSHOT).
#
#   ./scripts/install-snapshot.sh
#
# Why not `mvn install`: the poms still carry the released version (6.5.0, managed by the release
# workflow), so an install would overwrite the real 6.5.0 in ~/.m2 with 7.0 code (AGENTS.md). This
# builds in the reactor and installs copies of the poms with only the version changed, under a
# version no release uses.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

SNAPSHOT="7.0.0-SNAPSHOT"
CURRENT="$(mvn -q -N help:evaluate -Dexpression=project.version -DforceStdout)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

echo "install-snapshot: building $CURRENT in the reactor"
mvn -q -DskipTests package -pl all -am

# the poms with this branch's version replaced (project, parent and the managed dependencies)
pom() {
  sed "s|<version>$CURRENT</version>|<version>$SNAPSHOT</version>|g; s|\${project.version}|$SNAPSHOT|g" "$1" > "$2"
}

install() { # file pom [sources]
  local args=(-q install:install-file "-Dfile=$1" "-DpomFile=$2")
  if [ -n "${3:-}" ]; then args+=("-Dsources=$3"); fi
  mvn "${args[@]}"
}

pom pom.xml "$WORK/parent.pom"
install "$WORK/parent.pom" "$WORK/parent.pom"

pom core/pom.xml "$WORK/core.pom"
(cd core/src/main/java && zip -qr "$WORK/core-sources.jar" .)
install "core/target/scratch-$CURRENT.jar" "$WORK/core.pom" "$WORK/core-sources.jar"

for extension in tiled save recorder drawing; do
  pom "extensions/$extension/pom.xml" "$WORK/$extension.pom"
  install "extensions/$extension/target/scratch-$extension-$CURRENT.jar" "$WORK/$extension.pom"
done

# the fat jar, as projects carry it in +libs (core, every extension, LWJGL with its natives):
# what Studio compiles projects against
# with the sources of the core and every extension (Studio builds its API index from them)
for dir in core/src/main/java extensions/*/src/main/java; do
  (cd "$dir" && zip -qr "$WORK/all-sources.jar" . -x module-info.java)
done
mvn -q install:install-file "-Dfile=all/target/scratch-$CURRENT-all.jar" -DgroupId=org.openpatch \
  -DartifactId=scratch-all "-Dversion=$SNAPSHOT" -Dpackaging=jar -DgeneratePom=true \
  "-Dsources=$WORK/all-sources.jar"

echo "install-snapshot: org.openpatch:scratch{,-tiled,-save,-recorder,-drawing,-all}:$SNAPSHOT installed"
