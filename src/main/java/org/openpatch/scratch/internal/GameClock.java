package org.openpatch.scratch.internal;

/**
 * Game time: it stands still while the game is paused and runs slower or
 * faster with the game speed. Each frame asks how many game steps (calls of
 * every {@code run()}) it gets: at half speed every second frame gets one, at
 * double speed every frame gets two, so slow motion slows down code that moves
 * a fixed amount per frame as well as code that uses the frame time.
 *
 * <p>
 * Timers, gliding and animations follow this clock; the window itself
 * (transitions, the splash screen) keeps real time.
 */
public final class GameClock {

  /** Game time of one single step, when stepping a paused game. */
  static final double STEP_SECONDS = 1 / 60.0;
  /** Most steps one frame catches up, so a fast game cannot freeze the window. */
  static final int MAX_STEPS_PER_FRAME = 8;

  private double millis;
  private long steps;
  private double stepSeconds;
  private volatile boolean paused;
  private volatile int pendingSteps;
  private volatile double speed = 1;
  /** Steps owed to the speed: at half speed one every second frame. */
  private double credit;
  /** Game time owed to the next step(s), in seconds. */
  private double owed;

  /** Milliseconds of game time so far. */
  public double millis() {
    return this.millis;
  }

  /** Game steps so far: how often every run() was called. */
  public long steps() {
    return this.steps;
  }

  /** Game time of the current step in seconds (0 between steps of a paused game). */
  public double stepSeconds() {
    return this.stepSeconds;
  }

  public void pause() {
    this.paused = true;
  }

  public void resume() {
    this.pendingSteps = 0;
    this.paused = false;
  }

  public boolean isPaused() {
    return this.paused;
  }

  /** While paused: one step with the next frame. */
  public void step() {
    if (this.paused) {
      this.pendingSteps++;
    }
  }

  /** 1 is normal, 0.5 half as fast, 2 twice as fast; never below 0. */
  public void setSpeed(double speed) {
    this.speed = Math.max(0, speed);
  }

  public double getSpeed() {
    return this.speed;
  }

  /**
   * A frame passed: how many game steps it gets. {@link #stepSeconds()} is the
   * game time of each of them.
   *
   * @param realSeconds the real time since the last frame
   */
  public int frame(double realSeconds) {
    if (this.paused) {
      if (this.pendingSteps > 0) {
        this.pendingSteps--;
        this.stepSeconds = STEP_SECONDS;
        return 1;
      }
      this.stepSeconds = 0;
      return 0;
    }
    this.owed += realSeconds * this.speed;
    this.credit += this.speed;
    int steps = (int) Math.min(Math.floor(this.credit), MAX_STEPS_PER_FRAME);
    if (steps == 0) {
      this.stepSeconds = 0;
      return 0;
    }
    this.credit = Math.min(this.credit - steps, 1);
    this.stepSeconds = this.owed / steps;
    this.owed = 0;
    return steps;
  }

  /** Before each step: game time moves on by one step. */
  public void advance() {
    this.millis += this.stepSeconds * 1000;
    this.steps++;
  }
}
