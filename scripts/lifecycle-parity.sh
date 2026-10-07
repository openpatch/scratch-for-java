#!/usr/bin/env bash
# The identical Java source is run in Chromium by online-ide's test:browser.
set -euo pipefail
cd "$(dirname "$0")/.."
probe_dir="$(mktemp -d)"
trap 'rm -rf "$probe_dir"' EXIT
mvn -q -DskipTests compile dependency:build-classpath "-Dmdep.outputFile=$probe_dir/classpath"
cat > "$probe_dir/DesktopLifecycleLauncher.java" <<'JAVA'
public class DesktopLifecycleLauncher {
  public static void main(String[] args) {
    LifecycleProbe.main(args);
    org.openpatch.scratch.Window.getInstance().exit();
    System.exit(0);
  }
}
JAVA
probe_cp="target/classes:$(cat "$probe_dir/classpath")"
javac -cp "$probe_cp" -d "$probe_dir" compatibility/probes/LifecycleProbe.java "$probe_dir/DesktopLifecycleLauncher.java"
timeout 30s xvfb-run -a java -cp "$probe_dir:$probe_cp" DesktopLifecycleLauncher > "$probe_dir/output" 2> "$probe_dir/stderr"
diff -u compatibility/probes/lifecycle-expected.txt "$probe_dir/output"
echo 'Desktop clone lifecycle, removal, broadcasts and geometry: passed'
