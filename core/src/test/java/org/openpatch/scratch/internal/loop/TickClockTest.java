package org.openpatch.scratch.internal.loop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.function.IntToDoubleFunction;
import org.junit.jupiter.api.Test;

/** Synthetic frame times through the clock; ported from the spike's TickClockCheck. */
class TickClockTest {

  /** Ticks per frame (index 0..8) over 10 s of frames. */
  record Run(int frames, int ticks, double seconds, int[] histogram) {
    double perSecond() {
      return this.ticks / this.seconds;
    }
  }

  static Run run(TickClock clock, IntToDoubleFunction frameSeconds) {
    int[] histogram = new int[TickClock.MAX_TICKS_PER_FRAME + 1];
    int frames = 0, ticks = 0;
    double seconds = 0;
    while (seconds < 10) {
      double elapsed = frameSeconds.applyAsDouble(frames);
      int t = clock.advance(elapsed);
      histogram[t]++;
      ticks += t;
      frames++;
      seconds += elapsed;
    }
    return new Run(frames, ticks, seconds, histogram);
  }

  static TickClock clock(int hz, double speed) {
    TickClock clock = new TickClock();
    clock.useRefreshRate(hz);
    clock.setGameSpeed(speed);
    return clock;
  }

  @Test
  void aSixtyHertzScreenRunsOneTickPerFrame() {
    Run r = run(clock(60, 1), i -> 1 / 60.0);
    assertEquals(r.frames(), r.histogram()[1]);
  }

  @Test
  void jitterDoesNotGiveDoubleOrEmptyFrames() {
    Random random = new Random(1);
    Run r = run(clock(60, 1), i -> 1 / 60.0 + (random.nextDouble() - 0.5) * 0.003);
    assertEquals(r.frames(), r.histogram()[1]);
  }

  @Test
  void aScreenReportingSixtyHertzButRunningAt5994GetsOneTickPerFrame() {
    Run r = run(clock(60, 1), i -> 1 / 59.94);
    assertEquals(r.frames(), r.histogram()[1]);
  }

  @Test
  void fastScreensStillRunSixtyTicksPerSecond() {
    assertEquals(60, run(clock(144, 1), i -> 1 / 144.0).perSecond(), 1);
    assertEquals(60, run(clock(120, 1), i -> 1 / 120.0).perSecond(), 1);
  }

  @Test
  void slowMachinesCatchUp() {
    Run r = run(clock(60, 1), i -> 1 / 20.0);
    assertEquals(r.frames(), r.histogram()[3]);
    assertEquals(60, r.perSecond(), 1);
  }

  @Test
  void beyondEightTicksPerFrameTheGameSlowsDown() {
    Run r = run(clock(60, 1), i -> 1 / 5.0);
    assertEquals(r.frames(), r.histogram()[8]);
    assertTrue(r.perSecond() < 60);
  }

  @Test
  void gameSpeedChangesTheNumberOfTicksNotTheirLength() {
    assertEquals(120, run(clock(60, 2), i -> 1 / 60.0).perSecond(), 1);
    assertEquals(30, run(clock(60, 0.5), i -> 1 / 60.0).perSecond(), 1);
    assertEquals(0, run(clock(60, 0), i -> 1 / 60.0).ticks());
  }

  @Test
  void aLongFrameCountsAsAQuarterSecondAtMost() {
    TickClock clock = clock(60, 1);
    assertEquals(8, clock.advance(5.0));
    // the rest of the 0.25 s is dropped, apart from at most one tick
    assertTrue(clock.advance(1 / 60.0) <= 2);
  }

  @Test
  void pausedRunsNothingButStepRunsOneTick() {
    TickClock clock = clock(60, 1);
    clock.setPaused(true);
    assertEquals(0, clock.advance(1 / 60.0));
    clock.step();
    assertEquals(1, clock.advance(1 / 60.0));
    assertEquals(0, clock.advance(1 / 60.0));
    clock.setPaused(false);
    assertEquals(1, clock.advance(1 / 60.0));
  }

  @Test
  void settingTheSamePauseStateEveryFrameKeepsTheOwedTime() {
    // the window passes the game's pause flag every frame; tiny frames must still add up
    TickClock clock = clock(60, 1);
    int ticks = 0;
    for (int i = 0; i < 1000; i++) {
      clock.setPaused(false);
      ticks += clock.advance(0.001);
    }
    assertEquals(60, ticks);
  }

  @Test
  void gameSpeedIsLimited() {
    assertThrows(IllegalArgumentException.class, () -> new TickClock().setGameSpeed(9));
    assertThrows(IllegalArgumentException.class, () -> new TickClock().setGameSpeed(-1));
  }
}
