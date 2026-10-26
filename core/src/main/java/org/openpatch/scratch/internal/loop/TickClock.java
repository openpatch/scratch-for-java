package org.openpatch.scratch.internal.loop;

/**
 * Decides how many ticks the game runs in a frame. A tick is exactly
 * 1/60 s of game time; game speed changes how many ticks run per second, not their length.
 * Called once per frame on the window thread; not thread-safe.
 */
public final class TickClock {

  /** The length of a tick in seconds of game time. */
  public static final double TICK = 1 / 60.0;
  /** More ticks owed than this in one frame are dropped: the game slows down instead. */
  public static final int MAX_TICKS_PER_FRAME = 8;
  /** A frame longer than this (a breakpoint, a dragged window, a sleeping laptop) counts as this. */
  static final double MAX_FRAME = 0.25;

  private double refreshPeriod = TICK;
  private double gameSpeed = 1;
  private double owed;
  private boolean paused;
  private int steps;

  /**
   * The screen's refresh rate, for snapping frame times. A rate within 0.5 % of 60 Hz counts as
   * exactly 60 Hz, so a 59.94 Hz screen runs one tick per frame instead of a double tick every
   * 17 s; 0 or less means unknown (60 Hz is assumed).
   */
  public void useRefreshRate(int hz) {
    this.refreshPeriod = hz <= 0 || Math.abs(hz - 60) / 60.0 <= 0.005 ? TICK : 1.0 / hz;
  }

  /** Game speed between 0 and 8: how many ticks run per 1/60 s of real time. */
  public void setGameSpeed(double gameSpeed) {
    if (!(gameSpeed >= 0 && gameSpeed <= MAX_TICKS_PER_FRAME)) {
      throw new IllegalArgumentException("The game speed has to be between 0 and 8, not " + gameSpeed + ".");
    }
    this.gameSpeed = gameSpeed;
  }

  public double getGameSpeed() {
    return this.gameSpeed;
  }

  /** Pausing and resuming forget the time owed, so resuming starts without a burst of ticks. */
  public void setPaused(boolean paused) {
    if (this.paused != paused) {
      this.paused = paused;
      this.owed = 0;
    }
  }

  public boolean isPaused() {
    return this.paused;
  }

  /** While paused: run exactly one more tick in the next frame. */
  public void step() {
    this.steps++;
  }

  /**
   * The ticks to run for a frame that took {@code elapsed} seconds of real time since the last
   * one.
   */
  public int advance(double elapsed) {
    if (this.paused) {
      if (this.steps > 0) {
        this.steps--;
        return 1;
      }
      return 0;
    }
    elapsed = this.snap(Math.min(Math.max(elapsed, 0), MAX_FRAME));
    this.owed += elapsed * this.gameSpeed;
    // the epsilon keeps a sum of exact 1/60ths from landing a hair below a whole tick
    int ticks = (int) Math.min(Math.floor(this.owed / TICK + 1e-9), MAX_TICKS_PER_FRAME);
    this.owed = Math.max(0, this.owed - ticks * TICK);
    if (ticks == MAX_TICKS_PER_FRAME) {
      this.owed = Math.min(this.owed, TICK);
    }
    return ticks;
  }

  /** Near a whole number of refresh periods: exactly that many, so timer jitter changes nothing. */
  double snap(double elapsed) {
    long periods = Math.round(elapsed / this.refreshPeriod);
    if (periods >= 1 && Math.abs(elapsed - periods * this.refreshPeriod) < 0.1 * this.refreshPeriod) {
      return periods * this.refreshPeriod;
    }
    return elapsed;
  }
}
