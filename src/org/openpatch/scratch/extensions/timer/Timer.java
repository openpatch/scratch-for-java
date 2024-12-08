package org.openpatch.scratch.extensions.timer;

import org.openpatch.scratch.internal.Applet;

/**
 * The Timer class provides methods for timing events in a graphical application.
 * It allows checking if a specified number of milliseconds has passed since the last time
 * a method was called, or if a specified interval has passed.
 *
 * <p>Usage example:
 * <pre>
 * Timer timer = new Timer();
 * if (timer.everyMillis(1000)) {
 *   // Do something every second
 * }
 * if (timer.forMillis(5000)) {
 *   // Do something for 5 seconds
 * }
 * if (timer.afterMillis(2000)) {
 *   // Do something after 2 seconds
 * }
 * if (timer.intervalMillis(1000)) {
 *   // Do something every second
 * }
 * </pre>
 */
public class Timer {
  private int startMillisEvery;
  private int startMillisFor;
  private int startMillisAfter;
  private int startMillisInterval;
  private int currentInterval;

  /**
   * Constructs a new Timer object.
   */
  public Timer() {
    this.startMillisEvery = -1;
    this.startMillisFor = -1;
    this.startMillisAfter = -1;
    this.startMillisInterval = -1;
    this.currentInterval = 0;
  }

  /**
   * Resets the timer.
   */
  public void reset() {
    this.startMillisEvery = -1;
    this.startMillisFor = -1;
    this.startMillisAfter = -1;
    this.startMillisInterval = -1;
    this.currentInterval = 0;
  }

  /**
   * Returns the number of milliseconds since the program started.
   *
   * @return the number of milliseconds since the program started
   */
  public static int millis() {
    return Applet.getInstance().millis();
  }

  /**
   * Checks if the specified number of milliseconds has passed since the last time this method returned true.
   * 
   * @param millis The number of milliseconds to wait before returning true.
   * @return true if the specified number of milliseconds has passed since the last call that returned true, false otherwise.
   */
  public boolean everyMillis(int millis) {
    int nowMillis = millis();
    if (startMillisEvery < 0) {
      startMillisEvery = nowMillis;
    }
    if (nowMillis >= startMillisEvery + millis) {
      startMillisEvery = nowMillis;
      return true;
    }
    return false;
  }

  /**
   * Checks if the specified amount of milliseconds has passed since the method was first called.
   * If the method is called for the first time, it initializes the start time.
   *
   * @param millis the number of milliseconds to check against
   * @return true if the current time is less than the start time plus the specified milliseconds, false otherwise
   */
  public boolean forMillis(int millis) {
    int nowMillis = millis();
    if (startMillisFor < 0) {
      startMillisFor = nowMillis;
    }
    return nowMillis < startMillisFor + millis;
  }

  /**
   * Checks if the specified number of milliseconds has passed since the method was first called.
   *
   * @param millis the number of milliseconds to check against
   * @return true if the specified number of milliseconds has passed since the method was first called, false otherwise
   */
  public boolean afterMillis(int millis) {
    int nowMillis = millis();
    if (startMillisAfter < 0) {
      startMillisAfter = nowMillis;
    }
    return nowMillis >= startMillisAfter + millis;
  }

  /**
   * Checks if the specified interval in milliseconds has passed.
   *
   * @param millis the interval in milliseconds to check
   * @return true if the interval has passed, false otherwise
   */
  public boolean intervalMillis(int millis) {
    return this.intervalMillis(millis, millis, false);
  }

  /**
   * Checks if the specified interval in milliseconds has passed.
   *
   * @param millis the interval in milliseconds to check
   * @param skipFirst if true, the first interval will be skipped
   * @return true if the interval has passed, false otherwise
   */
  public boolean intervalMillis(int millis, boolean skipFirst) {
    return this.intervalMillis(millis, millis, skipFirst);
  }

  /**
   * Checks if the specified interval in milliseconds has passed.
   *
   * @param millis1 the first interval in milliseconds to check
   * @param millis2 the second interval in milliseconds to check
   * @return true if the interval has passed, false otherwise
   */
  public boolean intervalMillis(int millis1, int millis2) {
    return this.intervalMillis(millis1, millis2, false);
  }

  /**
   * Checks if the specified interval in milliseconds has passed.
   *
   * @param millis1 the first interval in milliseconds to check
   * @param millis2 the second interval in milliseconds to check
   * @param skipFirst if true, the first interval will be skipped
   * @return true if the interval has passed, false otherwise
   */
  public boolean intervalMillis(int millis1, int millis2, boolean skipFirst) {
    int nowMillis = millis();

    if (startMillisInterval < 0) {
      startMillisInterval = nowMillis;
      if (skipFirst) {
        startMillisInterval -= millis1;
      }
    }

    if (currentInterval == 0 && nowMillis < startMillisInterval + millis1) {
      return true;
    } else if (currentInterval == 0) {
      currentInterval = 1;
      startMillisInterval = nowMillis;
      return false;
    } else if (currentInterval == 1 && nowMillis < startMillisInterval + millis2) {
      return false;
    } else {
      currentInterval = 0;
      startMillisInterval = nowMillis;
      return true;
    }
  }
}
