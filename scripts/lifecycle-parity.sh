#!/usr/bin/env bash
# The identical Java source is run in Chromium by online-ide's test:browser.
set -euo pipefail
cd "$(dirname "$0")/.."
probe_dir="$(mktemp -d)"
trap 'rm -rf "$probe_dir"' EXIT
mvn -q -DskipTests compile dependency:build-classpath "-Dmdep.outputFile=$probe_dir/classpath"
cat > "$probe_dir/DesktopLifecycleLauncher.java" <<'JAVA'
public class DesktopLifecycleLauncher {
  public static void main(String[] args) throws InterruptedException {
    LifecycleProbe.main(args);
    // Let Processing finish native window initialization before requesting
    // shutdown. Exiting while JOGL is still opening races its display cleanup.
    org.openpatch.scratch.internal.Applet applet = org.openpatch.scratch.internal.Applet.getInstance();
    long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(10);
    while (applet.frameCount == 0) {
      if (System.nanoTime() >= deadline) throw new IllegalStateException("Window did not draw");
      Thread.sleep(10);
    }
    org.openpatch.scratch.Window.getInstance().exit();
  }
}
JAVA
probe_cp="target/classes:$(cat "$probe_dir/classpath")"
javac -cp "$probe_cp" -d "$probe_dir" compatibility/probes/LifecycleProbe.java "$probe_dir/DesktopLifecycleLauncher.java"
timeout 30s xvfb-run -a java --enable-native-access=ALL-UNNAMED -cp "$probe_dir:$probe_cp" DesktopLifecycleLauncher > "$probe_dir/output" 2> "$probe_dir/stderr"
diff -u compatibility/probes/lifecycle-expected.txt "$probe_dir/output"
echo 'Desktop clone lifecycle, removal, broadcasts and geometry: passed'
