package com.example.countdown;

import org.openpatch.scratch.Stage;

/**
 * A countdown shown in the top right corner of a stage. What learners use:
 *
 * <pre>{@code
 * Countdown.of(this).start(30);              // in the stage's constructor
 * if (Countdown.of(this).getSecondsLeft() < 5) { ... }
 * }</pre>
 *
 * <p>Every stage has its own countdown (an attachment, made the first time it is asked for). It
 * counts game time: ticks, so it stops while the game is paused and follows the game speed.
 */
public final class Countdown {

  /** Scratch for Java runs 60 ticks a second, whatever the screen's frame rate. */
  static final double TICK = 1.0 / 60;

  private double secondsLeft;
  private boolean running;
  private boolean shown = true;

  /**
   * The countdown of a stage.
   *
   * @param stage the stage
   * @return its countdown, made on first use
   */
  public static Countdown of(Stage stage) {
    return stage.extension(Countdown.class, Countdown::new);
  }

  /**
   * Starts counting down.
   *
   * @param seconds from how many seconds
   */
  public void start(double seconds) {
    this.secondsLeft = Math.max(0, seconds);
    this.running = this.secondsLeft > 0;
  }

  /** Stops where it is. */
  public void stop() {
    this.running = false;
  }

  /** @return the seconds left, 0 once it ran out */
  public double getSecondsLeft() {
    return this.secondsLeft;
  }

  /** @return whether it is counting */
  public boolean isRunning() {
    return this.running;
  }

  /** Shows the countdown in the corner (the default). */
  public void show() {
    this.shown = true;
  }

  /** Counts on without showing it. */
  public void hide() {
    this.shown = false;
  }

  /** @return whether it is shown in the corner */
  public boolean isShown() {
    return this.shown;
  }

  /** @return what the corner shows: whole seconds, rounded up, as "0:07" */
  public String getText() {
    int seconds = (int) Math.ceil(this.secondsLeft - 1e-9);
    return seconds / 60 + ":" + (seconds % 60 < 10 ? "0" : "") + seconds % 60;
  }

  /**
   * One tick of game time; called by the extension before the stage's tick.
   *
   * @return whether the countdown ran out in this tick
   */
  boolean tick() {
    if (!this.running) {
      return false;
    }
    this.secondsLeft = Math.max(0, this.secondsLeft - TICK);
    if (this.secondsLeft <= 1e-9) {
      this.secondsLeft = 0;
      this.running = false;
      return true;
    }
    return false;
  }
}
