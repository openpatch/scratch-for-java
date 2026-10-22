package org.openpatch.scratch;

import org.openpatch.scratch.internal.Game;

/**
 * A timer counting game time in seconds, like Scratch's timer: it stops while the game is paused
 * and runs faster at a higher game speed, so it agrees with everything else that happens in
 * ticks. Every stage and sprite has one ({@code getTimer()}); make more as fields with
 * {@code new Timer()}.
 *
 * <pre>{@code
 * private Timer shooting = new Timer();
 *
 * public void run() {
 *   if (this.isKeyPressed(KeyCode.SPACE) && this.shooting.every(0.25)) {
 *     this.shoot();
 *   }
 * }
 * }</pre>
 */
public class Timer {

  private double start;
  private double everyStart = Double.NaN, duringStart = Double.NaN, afterStart = Double.NaN;
  private double intervalStart = Double.NaN;
  private boolean intervalOff;

  /** A timer starting now. */
  public Timer() {
    this.start = now();
  }

  private static double now() {
    Game game = Game.currentOrNull();
    return game == null ? 0 : game.getTime();
  }

  /**
   * Starts the timer again from 0, and every method below with it.
   *
   * @example.files TimerReset.java
   * @scratchblock reset timer
   */
  public void reset() {
    this.start = now();
    this.everyStart = this.duringStart = this.afterStart = this.intervalStart = Double.NaN;
    this.intervalOff = false;
  }

  /** @return the seconds of game time since the timer started or was reset */
  public double getSeconds() {
    return now() - this.start;
  }

  /**
   * True once every number of seconds: the first time that many seconds after the first call,
   * then again each time as many seconds have passed.
   *
   * @param seconds the period
   * @return whether the period is over
   */
  public boolean every(double seconds) {
    double t = now();
    if (Double.isNaN(this.everyStart)) {
      this.everyStart = t;
    }
    if (t >= this.everyStart + seconds - 1e-9) {
      this.everyStart = t;
      return true;
    }
    return false;
  }

  /**
   * True for a number of seconds from the first call on, false afterwards.
   *
   * @param seconds how long
   * @return whether that time is still running
   */
  public boolean during(double seconds) {
    double t = now();
    if (Double.isNaN(this.duringStart)) {
      this.duringStart = t;
    }
    return t < this.duringStart + seconds - 1e-9;
  }

  /**
   * False for a number of seconds from the first call on, true afterwards.
   *
   * @param seconds how long to wait
   * @return whether that time is over
   */
  public boolean after(double seconds) {
    double t = now();
    if (Double.isNaN(this.afterStart)) {
      this.afterStart = t;
    }
    return t >= this.afterStart + seconds - 1e-9;
  }

  /**
   * Alternates: true for a number of seconds, then false for as long, and so on.
   *
   * @param seconds how long each phase lasts
   * @return whether it is in an "on" phase
   */
  public boolean interval(double seconds) {
    return this.interval(seconds, seconds);
  }

  /**
   * Alternates: true for {@code on} seconds, then false for {@code off} seconds, and so on.
   *
   * @param on how long it is true
   * @param off how long it is false
   * @return whether it is in an "on" phase
   */
  public boolean interval(double on, double off) {
    double t = now();
    if (Double.isNaN(this.intervalStart)) {
      this.intervalStart = t;
    }
    while (true) {
      double phase = this.intervalOff ? off : on;
      if (t < this.intervalStart + phase - 1e-9 || phase <= 0 && on <= 0 && off <= 0) {
        return !this.intervalOff;
      }
      this.intervalStart += phase;
      this.intervalOff = !this.intervalOff;
    }
  }
}
