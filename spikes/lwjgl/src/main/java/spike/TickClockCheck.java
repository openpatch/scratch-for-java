package spike;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.IntToDoubleFunction;

/**
 * Feeds GameLoopCheck.TickClock synthetic frame times, no window, no LWJGL,
 * and checks the tick clock's rates for 10 s per scenario.
 */
public class TickClockCheck {

  /** One scenario: name, frames, ticks, ticks per second of simulated time, histogram 0..8. */
  record Result(String name, int frames, int ticks, double tps, int[] histogram) {}

  public static void main(String[] args) {
    Random jitter = new Random(1);
    Result[] r = {
        run("60 Hz exact", 60, 1, i -> 1 / 60.0),
        run("60 Hz jitter", 60, 1, i -> 1 / 60.0 + (jitter.nextDouble() - 0.5) * 0.003),
        run("59.94 Hz", 60, 1, i -> 1 / 59.94),
        run("144 Hz", 144, 1, i -> 1 / 144.0),
        run("slow 20 fps", 60, 1, i -> 1 / 20.0),
        run("very slow 5 fps", 60, 1, i -> 1 / 5.0),
        run("double speed", 60, 2, i -> 1 / 60.0),
        run("half speed", 60, 0.5, i -> 1 / 60.0),
        run("hitch", 60, 1, i -> i == 300 ? 2.0 : 1 / 60.0),
    };
    for (Result result : r) {
      System.out.printf(Locale.ROOT, "%-15s %4d frames %5d ticks %7.2f ticks/s  %s%n",
          result.name(), result.frames(), result.ticks(), result.tps(), Arrays.toString(result.histogram()));
    }
    check(r);
    System.out.println("tick clock: all expectations hold");
  }

  /** A fresh clock fed 10 s of frames; the last frame may cross the mark a little. */
  private static Result run(String name, int refresh, double speed, IntToDoubleFunction frameSeconds) {
    GameLoopCheck.TickClock clock = new GameLoopCheck.TickClock();
    clock.useRefreshRate(refresh);
    clock.gameSpeed = speed;
    int[] histogram = new int[GameLoopCheck.MAX_TICKS_PER_FRAME + 1];
    int frames = 0, ticks = 0;
    double seconds = 0;
    while (seconds < 10) {
      double elapsed = frameSeconds.applyAsDouble(frames);
      int ticked = clock.advance(elapsed);
      histogram[ticked]++;
      ticks += ticked;
      frames++;
      seconds += elapsed;
    }
    return new Result(name, frames, ticks, ticks / seconds, histogram);
  }

  private static void check(Result[] r) {
    for (int i = 0; i <= 2; i++) {
      expect(r[i].histogram()[1] == r[i].frames(), r[i].name()
          + ": every frame must have exactly 1 tick, histogram is " + Arrays.toString(r[i].histogram()));
    }
    expect(Math.abs(r[3].tps() - 60) <= 1,
        "144 Hz: expected 60 ± 1 ticks/s, got " + r[3].tps());
    expect(r[4].histogram()[3] == r[4].frames() && Math.abs(r[4].tps() - 60) <= 1,
        "slow 20 fps: expected 3 ticks per frame and 60 ± 1 ticks/s, got histogram "
            + Arrays.toString(r[4].histogram()) + " and " + r[4].tps() + " ticks/s");
    expect(r[5].histogram()[8] == r[5].frames(),
        "very slow 5 fps: must cap at 8 ticks per frame, histogram is "
            + Arrays.toString(r[5].histogram()));
    expect(Math.abs(r[6].tps() - 120) <= 1,
        "double speed: expected 120 ± 1 ticks/s, got " + r[6].tps());
    expect(Math.abs(r[7].tps() - 30) <= 1,
        "half speed: expected 30 ± 1 ticks/s, got " + r[7].tps());
  }

  private static void expect(boolean ok, String what) {
    if (!ok) {
      throw new IllegalStateException("tick clock: " + what);
    }
  }
}
