#!/usr/bin/env bash
# The identical Java source is run in Chromium by online-ide's test:browser.
set -euo pipefail
cd "$(dirname "$0")/.."
probe_dir="$(mktemp -d)"
trap 'rm -rf "$probe_dir"' EXIT
# in the reactor, never `install` (AGENTS.md); the probe needs only the core
mvn -q -DskipTests compile dependency:build-classpath -pl core -am \
    -Dmdep.includeScope=runtime "-Dmdep.outputFile=$probe_dir/classpath"
cat > "$probe_dir/DesktopLifecycleLauncher.java" <<'JAVA'
public class DesktopLifecycleLauncher {
  public static void main(String[] args) throws InterruptedException {
    LifecycleProbe.main(args);
    // The probe's stage constructor prints everything on this thread, while the
    // window shows the stage from the next frame on (it also draws while
    // paused, see Window#pause). Wait until the game thread has taken the
    // stage, give the window a moment to draw it, then end the program.
    org.openpatch.scratch.Window window = org.openpatch.scratch.Window.getInstance();
    long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(10);
    while (window.getStage() == null) {
      if (System.nanoTime() >= deadline) throw new IllegalStateException("Stage was never shown");
      Thread.sleep(10);
    }
    Thread.sleep(200);
    window.exit();
  }
}
JAVA
probe_cp="core/target/classes:$(cat "$probe_dir/classpath")"
javac -cp "$probe_cp" -d "$probe_dir" compatibility/probes/LifecycleProbe.java "$probe_dir/DesktopLifecycleLauncher.java"
# the null audio backend: the same output with or without a sound card (CI has none)
ALSOFT_DRIVERS=null timeout --foreground 60s xvfb-run -a java --enable-native-access=ALL-UNNAMED -Dscratch4j.platform=x11 \
  -cp "$probe_dir:$probe_cp" DesktopLifecycleLauncher > "$probe_dir/output" 2> "$probe_dir/stderr"
diff -u compatibility/probes/lifecycle-expected.txt "$probe_dir/output"
echo 'Desktop clone lifecycle, removal, broadcasts and geometry: passed'
