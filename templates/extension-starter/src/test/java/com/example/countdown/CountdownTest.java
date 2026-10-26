package com.example.countdown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The countdown's own logic, without a window: what the extension's hooks call every tick. */
class CountdownTest {

  @Test
  void runsOutAfterSixtyTicksASecond() {
    Countdown countdown = new Countdown();
    countdown.start(2);
    int ranOut = 0;
    for (int tick = 1; tick <= 150; tick++) {
      if (countdown.tick()) {
        ranOut = tick;
      }
    }
    assertEquals(120, ranOut, "2 seconds are 120 ticks");
    assertFalse(countdown.isRunning());
    assertEquals(0, countdown.getSecondsLeft());
  }

  @Test
  void showsWholeSecondsRoundedUp() {
    Countdown countdown = new Countdown();
    countdown.start(75);
    assertEquals("1:15", countdown.getText());
    countdown.tick();
    assertEquals("1:15", countdown.getText(), "74.98 s still shows 1:15");
    countdown.start(0.5);
    assertEquals("0:01", countdown.getText());
  }

  @Test
  void stopsAndStartsAgain() {
    Countdown countdown = new Countdown();
    countdown.start(1);
    countdown.tick();
    countdown.stop();
    double left = countdown.getSecondsLeft();
    assertFalse(countdown.tick(), "a stopped countdown does not run out");
    assertEquals(left, countdown.getSecondsLeft());
    countdown.start(3);
    assertTrue(countdown.isRunning());
  }
}
