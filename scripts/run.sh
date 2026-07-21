#!/bin/bash
# Pick something to run and run it.
#
#   ./scripts/run.sh              choose from everything
#   ./scripts/run.sh cat          start with "cat" already typed
#                                 (a query matching exactly one thing runs it)
#   ./scripts/run.sh --demos      only the demos
#   ./scripts/run.sh --archives   only the finished tutorial projects
#   ./scripts/run.sh --reference  only the reference examples
#
# Demos and reference examples are run from target/classes. The tutorial
# projects under docs/archives are not part of the build, so they are compiled
# on the fly into target/archive-run.
set -e

ROOT=$(cd "$(dirname "$0")/.." && pwd)
cd "$ROOT"

if ! command -v fzf > /dev/null; then
  echo "fzf is not installed. See https://github.com/junegunn/fzf"
  exit 1
fi

want=all
query=
for arg in "$@"; do
  case "$arg" in
    --demos)     want=demo ;;
    --archives)  want=archive ;;
    --reference) want=ref ;;
    --help|-h)   sed -n '2,12p' "$0" | sed 's/^# \{0,1\}//'; exit 0 ;;
    *)           query="$arg" ;;
  esac
done

# ---------------------------------------------------------------- compile once
if [ ! -d target/classes ] || [ -z "$(find target/classes -name '*.class' -print -quit 2>/dev/null)" ]; then
  echo "Compiling the library and examples..."
  mvn -q compile
fi

CP_FILE=target/run-classpath.txt
if [ ! -f "$CP_FILE" ] || [ pom.xml -nt "$CP_FILE" ]; then
  echo "Working out the classpath..."
  mvn -q dependency:build-classpath -Dmdep.outputFile="$CP_FILE" > /dev/null
fi
CP="target/classes:$(cat "$CP_FILE")"

# ------------------------------------------------------------------- the menu
# Each line is:  <kind>\t<label>\t<what to run>
menu() {
  if [ "$want" = all ] || [ "$want" = demo ]; then
    grep -rl "static void main" src/examples/java/demos --include="*.java" 2>/dev/null | sort | while read -r f; do
      pkg=$(sed -n 's/^package \(.*\);/\1/p' "$f" | head -1)
      cls=$(basename "$f" .java)
      printf 'demo\t%s\t%s\n' "$(dirname "${f#src/examples/java/demos/}")" "${pkg:+$pkg.}$cls"
    done
  fi

  if [ "$want" = all ] || [ "$want" = archive ]; then
    for d in docs/archives/*/; do
      name=$(basename "$d")
      [ "$name" = build ] && continue
      entry=$(grep -l "static void main" "$d"*.java 2>/dev/null | head -1) || continue
      [ -n "$entry" ] && printf 'archive\t%s\t%s\n' "$name" "$(basename "$entry" .java)"
    done
  fi

  if [ "$want" = all ] || [ "$want" = ref ]; then
    grep -rl "static void main" src/examples/java/reference --include="*.java" 2>/dev/null | sort | while read -r f; do
      pkg=$(sed -n 's/^package \(.*\);/\1/p' "$f" | head -1)
      cls=$(basename "$f" .java)
      rel=${f#src/examples/java/reference/}
      # examples kept in a folder are named after the folder, not after the
      # MyWindow class every one of them happens to contain
      case "$rel" in
        */*) label=${rel%%/*} ;;
        *)   label=$cls ;;
      esac
      printf 'ref\t%s\t%s\n' "$label" "${pkg:+$pkg.}$cls"
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
          case "$1" in
            demo)    find src/examples/java/demos/$2 -name "*.java" | head -1 | xargs -r head -60 ;;
            archive) ls docs/archives/$2; echo; head -40 docs/archives/$2/$3.java ;;
            ref)     if [ -f src/examples/java/reference/$2.java ]; then
                       head -60 src/examples/java/reference/$2.java
                     else
                       ls src/examples/java/reference/$2 2>/dev/null
                       echo
                       head -40 src/examples/java/reference/$2/MySprite.java 2>/dev/null \
                         || head -40 src/examples/java/reference/$2/MyStage.java 2>/dev/null
                     fi ;;
          esac')

[ -z "$choice" ] && exit 0

kind=$(echo "$choice" | awk '{print $1}')
label=$(echo "$choice" | awk '{print $2}')
target=$(echo "$choice" | awk '{print $3}')

# ------------------------------------------------------------------- run it
if [ "$kind" = archive ]; then
  outdir="target/archive-run/$label"
  rm -rf "$outdir"; mkdir -p "$outdir"
  echo "Compiling docs/archives/$label ..."
  javac -nowarn -cp "$CP" -d "$outdir" docs/archives/"$label"/*.java
  # the archive folder joins the classpath so its own images and sounds are found
  echo "Running $target"
  exec java -cp "$outdir:docs/archives/$label:$CP" "$target"
else
  echo "Running $target"
  exec java -cp "$CP" "$target"
fi
