package org.openpatch.scratch.gamepad;

/**
 * What a stage or sprite is told about the controllers, at the same point of the tick as key
 * events (before {@code run()}): a button going down or up on any player's controller, and a
 * controller appearing in a player's slot. Like keys, events reach listener objects even while
 * the game is paused. A button that goes down and up between two ticks is not reported; ask
 * {@link Gamepad} in {@code run()} for the state that stays.
 *
 * <pre>{@code
 * public class Player extends Sprite implements GamepadListener {
 *   public void run() {
 *     this.changePosition(Gamepad.getStick(GamepadStick.LEFT).multiply(5));
 *   }
 *
 *   public void whenGamepadButtonPressed(GamepadButton button) {
 *     if (button == GamepadButton.A) {
 *       this.jump();
 *     }
 *   }
 * }
 * }</pre>
 *
 * @example.files GamepadExample.java
 */
public interface GamepadListener {

  /**
   * A button went down on one of the controllers.
   *
   * @param button the button that was pressed
   */
  default void whenGamepadButtonPressed(GamepadButton button) {}

  /**
   * A button came up on one of the controllers.
   *
   * @param button the button that was released
   */
  default void whenGamepadButtonReleased(GamepadButton button) {}

  /**
   * A controller appeared in a player's slot.
   *
   * @param player the player whose slot got a controller, 1 to 4
   */
  default void whenGamepadConnected(int player) {}
}
