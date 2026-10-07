package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameClockTest {

  private static final double FRAME = 1 / 60.0;

  /** Runs frames like the window does; returns the steps they got. */
  private static int frames(GameClock clock, int count) {
    int steps = 0;
    for (int i = 0; i < count; i++) {
      int n = clock.frame(FRAME);
      for (int s = 0; s < n; s++) {
        clock.advance();
      }
      steps += n;
    }
    return steps;
  }

  @Test
  void atNormalSpeedEveryFrameIsOneStepOfRealTime() {
    GameClock clock = new GameClock();
    assertEquals(60, frames(clock, 60));
    assertEquals(1000, clock.millis(), 1e-6);
    assertEquals(FRAME, clock.stepSeconds(), 1e-12);
    assertEquals(60, clock.steps());
  }

  @Test
  void aPausedGameStandsStillAndStepsOneFrameAtATime() {
    GameClock clock = new GameClock();
    frames(clock, 30);
    clock.pause();
    assertTrue(clock.isPaused());
    double before = clock.millis();
    assertEquals(0, frames(clock, 60));
    assertEquals(before, clock.millis(), 1e-9);

    clock.step();
    clock.step();
    assertEquals(2, frames(clock, 60), "two steps, then paused again");
    assertEquals(before + 2000 * GameClock.STEP_SECONDS, clock.millis(), 1e-6);

    clock.step();
    clock.resume();
    assertFalse(clock.isPaused());
    assertEquals(60, frames(clock, 60), "a step asked before resuming is not added on top");
  }

  @Test
  void halfSpeedIsEveryOtherFrameWithTwoFramesOfHalfTime() {
    GameClock clock = new GameClock();
    clock.setSpeed(0.5);
    assertEquals(30, frames(clock, 60));
    assertEquals(500, clock.millis(), 1e-6);
    assertEquals(FRAME, clock.stepSeconds(), 1e-12);
  }

  @Test
  void doubleSpeedIsTwoStepsAFrame() {
    GameClock clock = new GameClock();
    clock.setSpeed(2);
    assertEquals(120, frames(clock, 60));
    assertEquals(2000, clock.millis(), 1e-6);
  }

  @Test
  void aVeryFastGameStillLetsTheWindowDraw() {
    GameClock clock = new GameClock();
    clock.setSpeed(100);
    assertEquals(GameClock.MAX_STEPS_PER_FRAME, clock.frame(FRAME));
    clock.setSpeed(-3);
    assertEquals(0, clock.getSpeed());
  }

  @Test
  void nonFiniteSpeedCannotPoisonTimersOrFreezeTheWindow() {
    GameClock clock = new GameClock();
    for (double speed : new double[] { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY }) {
      clock.setSpeed(speed);
      assertEquals(0, clock.getSpeed());
      assertEquals(0, clock.frame(FRAME));
    }
    clock.setSpeed(1);
    assertEquals(1, clock.frame(-1));
    clock.advance();
    assertEquals(0, clock.millis());
  }
}
