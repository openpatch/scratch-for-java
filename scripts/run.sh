#!/bin/bash
# Pick something to run and run it.
#
#   ./scripts/run.sh              choose from everything
#   ./scripts/run.sh cat          start with "cat" already typed
#                                 (a query matching exactly one thing runs it)
#   ./scripts/run.sh --demos      only the demos
#   ./scripts/run.sh --archives   only the finished tutorial projects
#   ./scripts/run.sh --reference  only the reference examples
#   ./scripts/run.sh --each       every demo, one after the other: close the
#                                 window for the next one, Ctrl+C to stop
#   ./scripts/run.sh tiled -- -Dscratch4j.debugGl=true
#                                 everything after -- goes to java
#
# Demos and reference examples (examples/src/main/java) are built in the
# reactor and run from examples/target/classes, where their assets are copied
# to, so files they save end up in target, not in the sources. The tutorial
# projects under docs/archives are not part of the build: they are compiled on
# the fly into examples/target/archive-run.
set -e

ROOT=$(cd "$(dirname "$0")/.." && pwd)
cd "$ROOT"

want=all
query=
each=
jvm=()
while [ $# -gt 0 ]; do
  case "$1" in
    --demos)     want=demo ;;
    --archives)  want=archive ;;
    --reference) want=ref ;;
    --each)      each=1 ;;
    --help|-h)   sed -n '2,19p' "$0" | sed 's/^# \{0,1\}//'; exit 0 ;;
    --)          shift; jvm=("$@"); break ;;
    *)           query="$1" ;;
  esac
  shift
done

if [ -z "$each" ] && ! command -v fzf > /dev/null; then
  echo "fzf is not installed. See https://github.com/junegunn/fzf"
  exit 1
fi

SRC=examples/src/main/java
CLASSES="$ROOT/examples/target/classes"

# --------------------------------------------------------- incremental compile
# Never against ~/.m2: the version there is the released 6.x (AGENTS.md). With
# -am the reactor hands out core and the extensions as their target/classes.
echo "Building the library and the examples..."
mvn -q compile dependency:build-classpath -pl examples -am \
  -Dmdep.outputFile='${project.build.directory}/run-classpath.txt' -Dmdep.includeScope=runtime
CP="$CLASSES:$(cat examples/target/run-classpath.txt)"

JAVA_OPTS=(--enable-native-access=ALL-UNNAMED)
# GLFW wants the first thread on macOS
[ "$(uname)" = Darwin ] && JAVA_OPTS+=(-XstartOnFirstThread)
JAVA_OPTS+=("${jvm[@]}")

# Prints "<package.Class>" for every file with a main method below a folder.
mains() {
  grep -rlE "static void main|^void main" "$1" --include="*.java" 2>/dev/null | sort | while read -r f; do
    pkg=$(sed -n 's/^package \(.*\);/\1/p' "$f" | head -1)
    printf '%s\t%s\n' "$f" "${pkg:+$pkg.}$(basename "$f" .java)"
  done
}

# Runs a class from examples/target/classes; $1 is the folder to run in.
run_class() {
  echo "Running $2"
  (cd "$1" && java "${JAVA_OPTS[@]}" -cp "$CP" "$2")
}

# ------------------------------------------------------------------ --each
if [ -n "$each" ]; then
  mains "$SRC/demos" | while IFS=$'\t' read -r f cls; do
    [ -n "$query" ] && [[ "$cls" != *"$query"* ]] && continue
    echo
    echo "=== ${f#$SRC/demos/}  (close the window for the next demo, Ctrl+C to stop)"
    run_class "$CLASSES" "$cls" < /dev/null || echo "--- $cls ended with an error"
  done
  exit 0
fi

# ------------------------------------------------------------------- the menu
# Each line is:  <kind>\t<label>\t<what to run>
menu() {
  if [ "$want" = all ] || [ "$want" = demo ]; then
    mains "$SRC/demos" | while IFS=$'\t' read -r f cls; do
      printf 'demo\t%s\t%s\n' "$(dirname "${f#$SRC/demos/}")" "$cls"
    done
  fi

  if [ "$want" = all ] || [ "$want" = archive ]; then
    for d in docs/archives/*/; do
      name=$(basename "$d")
      [ "$name" = build ] && continue
      entry=$(grep -lE "static void main|^void main" "$d"*.java 2>/dev/null | head -1) || continue
      [ -n "$entry" ] && printf 'archive\t%s\t%s\n' "$name" "$(basename "$entry" .java)"
    done
  fi

  if [ "$want" = all ] || [ "$want" = ref ]; then
    mains "$SRC/reference" | while IFS=$'\t' read -r f cls; do
      rel=${f#$SRC/reference/}
      # examples kept in a folder are named after the folder, not after the
      # MyWindow class every one of them happens to contain
      case "$rel" in
        */*) label=${rel%%/*} ;;
        *)   label=$(basename "$f" .java) ;;
      esac
      printf 'ref\t%s\t%s\n' "$label" "$cls"
    done
  fi
}

choice=$(menu | column -t -s$'\t' \
  | fzf --query="$query" --height=80% --reverse --ansi \
        --select-1 --exit-0 \
        --prompt="run > " \
        --header="enter to run, esc to quit" \
        --preview-window=right:55%:wrap \
        --preview='
          set -- $(echo {});
          src=examples/src/main/java
          case "$1" in
            demo)    f=$(grep -rlE "static void main|^void main" $src/demos/$2 --include="*.java" | head -1)
                     head -60 "$f" ;;
            archive) ls docs/archives/$2; echo; head -40 docs/archives/$2/$3.java ;;
            ref)     if [ -f $src/reference/$2.java ]; then
                       head -60 $src/reference/$2.java
                     else
                       ls $src/reference/$2 2>/dev/null
                       echo
                       head -40 $src/reference/$2/MySprite.java 2>/dev/null \
                         || head -40 $src/reference/$2/MyStage.java 2>/dev/null
                     fi ;;
          esac')

[ -z "$choice" ] && exit 0

kind=$(echo "$choice" | awk '{print $1}')
label=$(echo "$choice" | awk '{print $2}')
target=$(echo "$choice" | awk '{print $3}')

# ------------------------------------------------------------------- run it
case "$kind" in
  archive)
    outdir="$ROOT/examples/target/archive-run/$label"
    rm -rf "$outdir"
    echo "Compiling ..."
    javac -nowarn -cp "$CP" -d "$outdir" docs/archives/"$label"/*.java
    # run in the project folder, so its own images and sounds are found
    echo "Running $target"
    cd "docs/archives/$label"
    exec java "${JAVA_OPTS[@]}" -cp "$outdir:.:$CP" "$target"
    ;;
  ref)
    # the reference examples load their assets relative to the reference folder
    run_class "$CLASSES/reference" "$target"
    ;;
  *)
    run_class "$CLASSES" "$target"
    ;;
esac
