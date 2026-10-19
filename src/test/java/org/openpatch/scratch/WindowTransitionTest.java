package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.openpatch.scratch.internal.Applet;

@EnabledIfEnvironmentVariable(named = "DISPLAY", matches = ".+")
class WindowTransitionTest {
  @TempDir Path directory;

  @Test
  void supplierRunsAtBlackAndBusyRequestsDoNotEvaluateIt() throws Exception {
    Path log = directory.resolve("transition.log");
    Process probe = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
        "-cp", System.getProperty("surefire.test.class.path", System.getProperty("java.class.path")),
        Probe.class.getName()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
    try {
      assertTrue(probe.waitFor(30, TimeUnit.SECONDS), () -> "Transition probe timed out: " + read(log));
      assertEquals(0, probe.exitValue(), () -> read(log));
      assertTrue(read(log).contains("TRANSITION_PASS"), () -> read(log));
    } finally {
      if (probe.isAlive()) probe.destroyForcibly();
    }
  }

  private static String read(Path path) {
    try { return Files.readString(path); } catch (Exception error) { return error.toString(); }
  }

  public static class Probe extends Stage {
    private static Window window;
    private static int factories;
    private static boolean blackShown;
    private boolean started;

    public Probe() {
      setColor(220);
      Applet.getInstance().registerMethod("post", this);
    }

    @Override public void run() {
      try {
        if (started) throw new AssertionError("Source stage ran during transition");
        started = true;
        window.transitionToStage(() -> {
          if (!Window.isTransitioning() || !blackShown || window.getStage() != this) {
            throw new AssertionError("Factory ran before black or outside its transition");
          }
          factories++;
          window.transitionToStage(() -> { throw new AssertionError("Reentrant factory ran"); }, 30);
          return new Destination();
        }, 30);
        if (factories != 0 || !Window.isTransitioning()) throw new AssertionError("Factory ran eagerly");
        window.transitionToStage(() -> { throw new AssertionError("Busy factory ran"); }, 30);
      } catch (Throwable error) { fail(error); }
    }

    public void post() {
      if (started && factories == 0 && Window.isTransitioning()) {
        int pixel = Applet.getInstance().getGraphics().get(160, 100);
        if ((pixel & 0xffffff) == 0) blackShown = true;
      }
    }

    public static void main(String[] args) {
      window = new Window(320, 200);
      window.setStage(new Probe());
    }
  }

  public static class Destination extends Stage {
    private boolean sawFadeIn;

    @Override public void run() {
      try {
        if (Window.isTransitioning()) {
          sawFadeIn = true;
          Probe.window.transitionToStage(() -> { throw new AssertionError("Fade-in factory ran"); }, 30);
        } else {
          if (!sawFadeIn || Probe.factories != 1) throw new AssertionError("Transition lifecycle mismatch");
          System.out.println("TRANSITION_PASS");
          Runtime.getRuntime().halt(0);
        }
      } catch (Throwable error) { fail(error); }
    }
  }

  private static void fail(Throwable error) {
    error.printStackTrace();
    Runtime.getRuntime().halt(1);
  }
}
