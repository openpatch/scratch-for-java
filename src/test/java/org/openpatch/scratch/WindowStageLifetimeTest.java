package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

@EnabledIfEnvironmentVariable(named = "DISPLAY", matches = ".+")
class WindowStageLifetimeTest {
  @TempDir Path directory;

  @Test
  void discardedStagesAreCollectedAndRetainedStagesCanBeReused() throws Exception {
    Path log = directory.resolve("stage-lifetime.log");
    Process probe = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
        "-cp", System.getProperty("surefire.test.class.path", System.getProperty("java.class.path")),
        Probe.class.getName()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
    try {
      assertTrue(probe.waitFor(30, TimeUnit.SECONDS), () -> "Stage lifetime probe timed out: " + read(log));
      assertEquals(0, probe.exitValue(), () -> read(log));
      assertTrue(read(log).contains("LIFETIME_PASS"), () -> read(log));
    } finally {
      if (probe.isAlive()) probe.destroyForcibly();
    }
  }

  private static String read(Path path) {
    try { return Files.readString(path); } catch (Exception error) { return error.toString(); }
  }

  public static class Probe extends Stage {
    private static Window window;
    private static Probe control;
    private static Saved saved;
    private static WeakReference<Stage> discarded;
    private int phase;
    private long collectionStarted;

    @Override public void run() {
      try {
        if (phase == 0) {
          saved = new Saved();
          Stage temporary = new Temporary();
          temporary.add(new Sprite());
          temporary.add(new Pen());
          temporary.add(new Text("temporary", 0, 0, 100));
          discarded = new WeakReference<>(temporary);
          phase = 1;
          window.setStage(temporary);
        } else if (phase == 1) {
          if (saved.visits != 1) throw new AssertionError("Retained stage did not run");
          if (collectionStarted == 0) collectionStarted = System.nanoTime();
          System.gc();
          if (discarded.get() != null) {
            if (System.nanoTime() - collectionStarted > TimeUnit.SECONDS.toNanos(8)) {
              throw new AssertionError("The stage registry retained a discarded stage");
            }
            return;
          }
          phase = 2;
          window.setStage(saved);
        } else {
          if (saved.visits != 2) throw new AssertionError("Retained stage lost its hooks or state");
          System.out.println("LIFETIME_PASS");
          Runtime.getRuntime().halt(0);
        }
      } catch (Throwable error) {
        error.printStackTrace();
        Runtime.getRuntime().halt(1);
      }
    }

    public static void main(String[] args) {
      window = new Window(320, 200);
      control = new Probe();
      window.setStage(control);
    }
  }

  public static class Temporary extends Stage {
    @Override public void run() { Probe.window.setStage(Probe.saved); }
  }

  public static class Saved extends Stage {
    private int visits;
    @Override public void run() {
      visits++;
      Probe.window.setStage(Probe.control);
    }
  }
}
