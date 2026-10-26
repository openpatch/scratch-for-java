package benchmark;

import demos.stressTest.Dino;
import demos.stressTest.Figure;
import demos.stressTest.FigureState;
import demos.stressTest.Knight;
import demos.stressTest.Ninja;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import org.openpatch.scratch.Color;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

/**
 * The scenes of {@code scripts/benchmark.sh}, the same source for 6.x and 7.0 (it uses only API
 * both have): measures updates and frames per second, CPU and the cost of the checks, then ends.
 *
 * <ul>
 *   <li>{@code stress}: the stress-test demo, every figure running (as after pressing space)
 *   <li>{@code collide}: n turning sprites, each asking every update which of the others it
 *       touches (n - 1 hitbox checks each)
 *   <li>{@code color}: n sprites, each asking every update whether it touches a colour that is
 *       nowhere (a full colour check)
 * </ul>
 */
public class Bench extends Stage {
  public static String version, scene;
  public static int n, width, height;
  /** frames counted by 7.0's frame hook; stays -1 in 6.x, where every frame is one update */
  public static volatile long frames = -1;
  static long checkNanos, checks;

  private static final double WARMUP = 3, MEASURE = 8;
  private long started, measureFrom, updates, framesAtStart, cpuAtStart;

  public Bench() {
    if (scene.equals("stress")) {
      this.addBackdrop("outback", "demos/stressTest/assets/outback.png");
      for (int i = 0; i < n; i++) {
        Figure f = i % 3 == 0 ? new Dino() : i % 3 == 1 ? new Knight() : new Ninja();
        f.state = FigureState.RUN; // as after pressing space: every figure runs
        this.add(f);
      }
    } else {
      this.addBackdrop("outback", "demos/stressTest/assets/outback.png");
      for (int i = 0; i < n; i++) {
        this.add(scene.equals("collide") ? new Ball() : new Seeker());
      }
    }
  }

  static long cpu() {
    return ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
  }

  public void run() {
    long now = System.nanoTime();
    if (started == 0) {
      started = now;
    }
    if (measureFrom == 0 && now - started >= WARMUP * 1e9) {
      measureFrom = now;
      updates = 0;
      framesAtStart = frames;
      cpuAtStart = cpu();
      checkNanos = 0;
      checks = 0;
    }
    if (measureFrom == 0) {
      return;
    }
    updates++;
    double seconds = (now - measureFrom) / 1e9;
    if (seconds >= MEASURE) {
      double ups = updates / seconds;
      double fps = frames < 0 ? ups : (frames - framesAtStart) / seconds;
      double cores = (cpu() - cpuAtStart) / 1e9 / seconds;
      double perUpdateMs = updates == 0 ? 0 : checkNanos / 1e6 / updates;
      double perCheckNs = checks == 0 ? 0 : (double) checkNanos / checks;
      System.out.println(String.format(Locale.ROOT,
          "RESULT version=%s scene=%s n=%d updates=%.1f frames=%.1f cpu=%.2f checkMs=%.3f nsPerCheck=%.1f",
          version, scene, n, ups, fps, cores, perUpdateMs, perCheckNs));
      System.out.flush();
      if (Boolean.getBoolean("benchmark.exit")) {
        System.exit(0); // shutdown hooks run: a profiler can write its recording
      }
      Runtime.getRuntime().halt(0); // 6.x may hang in a normal exit
    }
  }

  /** Moves, turns, and asks which of the others it touches: n - 1 hitbox checks. */
  public static class Ball extends Sprite {
    public Ball() {
      this.addCostume("bunny1_stand");
      this.setSize(40);
      this.setPosition(this.pickRandom(-width / 2 + 40, width / 2 - 40), this.pickRandom(-height / 2 + 40, height / 2 - 40));
      this.setDirection(this.pickRandom(0, 359));
    }

    public void run() {
      this.move(2);
      this.ifOnEdgeBounce();
      this.turnRight(1);
      long t = System.nanoTime();
      this.getTouchingSprites(Ball.class);
      checkNanos += System.nanoTime() - t;
      checks += n - 1;
    }
  }

  /** Moves and asks whether it touches a colour that is nowhere: a full colour check. */
  public static class Seeker extends Sprite {
    private static final Color NOWHERE = new Color(1, 254, 3);

    public Seeker() {
      this.addCostume("bunny1_stand");
      this.setSize(40);
      this.setPosition(this.pickRandom(-width / 2 + 40, width / 2 - 40), this.pickRandom(-height / 2 + 40, height / 2 - 40));
      this.setDirection(this.pickRandom(0, 359));
    }

    public void run() {
      this.move(2);
      this.ifOnEdgeBounce();
      long t = System.nanoTime();
      this.isTouchingColor(NOWHERE);
      checkNanos += System.nanoTime() - t;
      checks++;
    }
  }
}
