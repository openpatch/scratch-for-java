package com.example.countdown;

/**
 * Implemented by a stage or sprite that wants to know when the stage's countdown reaches zero,
 * like a {@code whenKeyPressed}:
 *
 * <pre>{@code
 * public class Player extends Sprite implements WhenTimeIsUp {
 *   public void whenTimeIsUp() {
 *     this.say("Too late!");
 *   }
 * }
 * }</pre>
 */
public interface WhenTimeIsUp {

  /** Called once, in the tick the countdown reaches zero. */
  void whenTimeIsUp();
}
