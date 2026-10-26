package org.openpatch.scratch.gamepad;

import org.openpatch.scratch.Vector2;

/**
 * Asks the connected controllers, like a gamepad's blocks in other Scratch dialects. Players are
 * numbered 1 to 4 in the order the controllers sit in the system's joystick slots; a controller
 * without a known layout (an unmapped pad) is skipped. Without a player number, every method
 * looks at all controllers: {@link #isButtonPressed(GamepadButton)} is true if any controller has
 * the button down, and {@link #getStick(GamepadStick)} and {@link #getTrigger(GamepadTrigger)}
 * read the first connected controller.
 *
 * <p>Sticks are a {@link Vector2} with x from −1 (left) to 1 (right) and y from −1 (down) to
 * 1 (up), as on the stage; near the middle they report (0, 0). Triggers go from 0 (not pressed)
 * to 1 (fully pressed). Buttons are in Xbox positions; see {@link GamepadButton} for the
 * PlayStation equivalents. Controllers connect and disconnect while the game runs; to react to
 * that and to button presses, implement {@link GamepadListener} on the stage or a sprite.
 *
 * <p>Before a window exists or while no controller is connected, queries return {@code false},
 * zero or (0, 0), so a game can ask without checking first.
 *
 * @example.files GamepadExample.java
 */
public final class Gamepad {

  private Gamepad() {}

  /** Whether any controller is connected. */
  public static boolean isConnected() {
    for (int player = 1; player <= GamepadState.PLAYERS; player++) {
      if (isConnected(player)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether a controller sits in the player's slot.
   *
   * @param player the player, 1 to 4
   * @return whether that slot has a controller
   */
  public static boolean isConnected(int player) {
    return GamepadExtension.now.connected(player);
  }

  /**
   * Whether any controller has the button down.
   *
   * @param button the button to ask about
   * @return whether it is down on any connected controller
   */
  public static boolean isButtonPressed(GamepadButton button) {
    for (int player = 1; player <= GamepadState.PLAYERS; player++) {
      if (isButtonPressed(player, button)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Whether the player's controller has the button down.
   *
   * @param player the player, 1 to 4
   * @param button the button to ask about
   * @return whether it is down on that player's controller
   */
  public static boolean isButtonPressed(int player, GamepadButton button) {
    return GamepadExtension.now.pressed(player, button);
  }

  /**
   * Where the first connected controller's stick points, (0, 0) if there is none.
   *
   * @param stick the stick to ask about
   * @return its position, x −1 to 1 and y −1 to 1 (up is positive)
   */
  public static Vector2 getStick(GamepadStick stick) {
    for (int player = 1; player <= GamepadState.PLAYERS; player++) {
      if (isConnected(player)) {
        return getStick(player, stick);
      }
    }
    return new Vector2(0, 0);
  }

  /**
   * Where the player's controller's stick points, (0, 0) if it is not connected.
   *
   * @param player the player, 1 to 4
   * @param stick the stick to ask about
   * @return its position, x −1 to 1 and y −1 to 1 (up is positive)
   */
  public static Vector2 getStick(int player, GamepadStick stick) {
    return new Vector2(GamepadExtension.now.stickX(player, stick),
        GamepadExtension.now.stickY(player, stick));
  }

  /**
   * How far down the first connected controller's trigger is, 0 if there is none.
   *
   * @param trigger the trigger to ask about
   * @return how far it is pressed, 0 (not at all) to 1 (fully)
   */
  public static double getTrigger(GamepadTrigger trigger) {
    for (int player = 1; player <= GamepadState.PLAYERS; player++) {
      if (isConnected(player)) {
        return getTrigger(player, trigger);
      }
    }
    return 0;
  }

  /**
   * How far down the player's controller's trigger is, 0 if it is not connected.
   *
   * @param player the player, 1 to 4
   * @param trigger the trigger to ask about
   * @return how far it is pressed, 0 (not at all) to 1 (fully)
   */
  public static double getTrigger(int player, GamepadTrigger trigger) {
    return GamepadExtension.now.trigger(player, trigger);
  }
}
