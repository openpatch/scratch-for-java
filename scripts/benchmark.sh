#!/bin/bash
# Benchmarks the library and appends every result to scripts/benchmark/history.csv.
#
#   ./scripts/benchmark.sh               the full set of scenes, windows on this display
#   ./scripts/benchmark.sh --quick       one size per scene
#   ./scripts/benchmark.sh --xvfb        under Xvfb (software OpenGL: a weak PC, not a GPU)
#   ./scripts/benchmark.sh --with-6x     also 6.x, built from `main` in a worktree under
#                                        examples/target/benchmark-6x (first time: a few minutes)
#   ./scripts/benchmark.sh --note "…"    a remark stored with the results
#   ./scripts/benchmark.sh --dry-run     print the results, leave the history alone
#
# The scenes are examples/src/main/java/benchmark/Bench.java: the stress-test demo, n sprites
# that each ask which of the others they touch, and n sprites that each look for a colour.
# Each run warms up for 3 s and measures 8 s. In 6.x every frame is one update; in 7.0 the game
# runs 60 updates a second if it can, and the frames are counted separately.
#
# History columns: date, commit, dirty (core/extensions/examples changed), version, scene, n,
# updates/s, frames/s, CPU cores busy, check time per update (ms), time per check (ns),
# display, OpenGL renderer, CPU, Java, note. Commit the history with the change it measures.
set -e

ROOT=$(cd "$(dirname "$0")/.." && pwd)
cd "$ROOT"
HISTORY="$ROOT/scripts/benchmark/history.csv"
V6="$ROOT/examples/target/benchmark-6x"

xvfb=
with6=
quick=
dry=
note=
while [ $# -gt 0 ]; do
  case "$1" in
    --xvfb) xvfb=1 ;;
    --with-6x) with6=1 ;;
    --quick) quick=1 ;;
    --dry-run) dry=1 ;;
    --note) note="$2"; shift ;;
    -h|--help) sed -n '2,20p' "$0" | sed 's/^# \{0,1\}//'; exit 0 ;;
    *) echo "unknown option: $1 (see --help)"; exit 1 ;;
  esac
  shift
done

if [ -n "$quick" ]; then
  SCENES=("stress 540" "collide 400" "color 50")
else
  SCENES=("stress 540" "stress 1080" "stress 2160" "collide 200" "collide 400" "collide 800"
          "color 10" "color 50" "color 100")
fi

# ------------------------------------------------------------------------- build
# in the reactor, never against ~/.m2 (AGENTS.md)
echo "benchmark: building the library and the examples"
mvn -q compile dependency:build-classpath -pl examples -am \
  -Dmdep.outputFile='${project.build.directory}/run-classpath.txt' -Dmdep.includeScope=runtime
CP7="$ROOT/examples/target/classes:$(cat examples/target/run-classpath.txt)"

if [ -n "$with6" ]; then
  if [ ! -f "$V6/pom.xml" ]; then
    echo "benchmark: building 6.x from main (once)"
    git worktree prune
    git worktree add -q --detach "$V6" main
    (cd "$V6" && mvn -q package -Pall -DskipTests > /dev/null)
  fi
  JAR6=$(ls "$V6"/target/scratch-*-all.jar | head -1)
  commit6=$(git -C "$V6" rev-parse --short HEAD)
  mkdir -p "$V6/benchmark-classes"
  javac -nowarn -encoding UTF-8 -cp "$JAR6" -d "$V6/benchmark-classes" \
    "$V6"/src/examples/java/demos/stressTest/*.java examples/src/main/java/benchmark/Bench.java \
    scripts/benchmark/Main6.java
fi

# ------------------------------------------------------------------ where it runs
commit=$(git rev-parse --short HEAD)
dirty=no
[ -n "$(git status --porcelain -- core extensions examples)" ] && dirty=yes
cpu=$(grep -m1 "model name" /proc/cpuinfo | sed 's/.*: //')
java=$(java -version 2>&1 | head -1 | sed 's/.*version "\([^"]*\)".*/\1/')

start_display() {
  if [ -n "$xvfb" ]; then
    XD=$((90 + RANDOM % 9))
    Xvfb ":$XD" -screen 0 1400x1000x24 > /dev/null 2>&1 &
    XP=$!
    sleep 1
    export DISPLAY=":$XD" XDG_SESSION_TYPE=x11 WAYLAND_DISPLAY=
    PLATFORM=(-Dscratch4j.platform=x11)
  else
    PLATFORM=()
  fi
}
stop_display() {
  if [ -n "$xvfb" ]; then
    kill "$XP" 2> /dev/null
    wait "$XP" 2> /dev/null || true
  fi
}

if [ -n "$xvfb" ]; then
  where=xvfb
  start_display
  renderer=$(glxinfo -B 2> /dev/null | sed -n 's/.*OpenGL renderer string: //p')
  stop_display
else
  where="${XDG_SESSION_TYPE:-x11}"
  renderer=$(glxinfo -B 2> /dev/null | sed -n 's/.*OpenGL renderer string: //p')
fi
renderer=${renderer:-unknown}

[ -n "$dry" ] || [ -f "$HISTORY" ] || echo "date,commit,dirty,version,scene,n,updates_per_s,frames_per_s,cpu_cores,check_ms_per_update,ns_per_check,display,renderer,cpu,java,note" > "$HISTORY"

csv() { printf '"%s"' "${1//\"/\"\"}"; }

# ---------------------------------------------------------------------- the runs
run() { # version scene n
  start_display
  if [ "$1" = 6 ]; then
    out=$(cd "$V6/src/examples/java" && timeout 120 java --enable-native-access=ALL-UNNAMED \
      -cp "$V6/benchmark-classes:$JAR6" Main6 "$2" "$3" 2>&1) || true
  else
    out=$(cd "$ROOT/examples/target/classes" && timeout 120 java --enable-native-access=ALL-UNNAMED \
      "${PLATFORM[@]}" -cp "$CP7" benchmark.Benchmark "$2" "$3" 2>&1) || true
  fi
  stop_display
  line=$(echo "$out" | grep -m1 '^RESULT') || true
  if [ -z "$line" ]; then
    echo "  $1 $2 $3: no result"
    echo "$out" | grep -v WARNING | tail -3 | sed 's/^/    /'
    return
  fi
  # RESULT version=… scene=… n=… updates=… frames=… cpu=… checkMs=… nsPerCheck=…
  declare -A r
  for kv in ${line#RESULT }; do r[${kv%%=*}]=${kv#*=}; done
  printf '  %-4s %-8s %5s  updates/s %6s  frames/s %6s  cpu %5s  checks %8s ms/update  %10s ns/check\n' \
    "${r[version]}" "${r[scene]}" "${r[n]}" "${r[updates]}" "${r[frames]}" "${r[cpu]}" "${r[checkMs]}" "${r[nsPerCheck]}"
  if [ -z "$dry" ]; then
    local c=$commit d=$dirty
    [ "$1" = 6 ] && c=$commit6 d=no # 6.x: the commit of main it was built from
    echo "$(date -u +%Y-%m-%dT%H:%M:%SZ),$c,$d,${r[version]},${r[scene]},${r[n]},${r[updates]},${r[frames]},${r[cpu]},${r[checkMs]},${r[nsPerCheck]},$where,$(csv "$renderer"),$(csv "$cpu"),$java,$(csv "$note")" >> "$HISTORY"
  fi
}

echo "benchmark: $commit$([ $dirty = yes ] && echo ' (with changes)') on $where, $renderer"
for scene in "${SCENES[@]}"; do
  if [ -n "$with6" ]; then
    run 6 $scene
  fi
  run 7 $scene
done
if [ -z "$dry" ]; then
  echo "benchmark: appended to scripts/benchmark/history.csv"
fi
