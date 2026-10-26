package org.openpatch.scratch.gamepad;

/**
 * What the controllers were like at one point in time: every player slot's buttons, sticks and
 * triggers. Not part of the learner API: the extension fills one per poll on the window thread,
 * and the game thread's copy is what learners read.
 *
 * @ignore-in-docs
 */
public final class GamepadState {

  /** The player slots, 1 to {@value #PLAYERS} in joystick-slot order. */
  public static final int PLAYERS = 4;

  private final boolean[] connected = new boolean[PLAYERS];
  private final boolean[][] buttons = new boolean[PLAYERS][GamepadButton.values().length];
  // left stick x, y, then right stick x, y, per player
  private final double[][] sticks = new double[PLAYERS][GamepadStick.values().length * 2];
  private final double[][] triggers = new double[PLAYERS][GamepadTrigger.values().length];

  /** No controller connected, nothing pressed, sticks and triggers at rest. */
  public GamepadState() {}

  /** Empties the whole state. */
  public void clear() {
    java.util.Arrays.fill(this.connected, false);
    for (int p = 0; p < PLAYERS; p++) {
      java.util.Arrays.fill(this.buttons[p], false);
      java.util.Arrays.fill(this.sticks[p], 0);
      java.util.Arrays.fill(this.triggers[p], 0);
    }
  }

  /** Copies {@code from} into this state, replacing everything. */
  public void copyFrom(GamepadState from) {
    for (int p = 0; p < PLAYERS; p++) {
      this.connected[p] = from.connected[p];
      System.arraycopy(from.buttons[p], 0, this.buttons[p], 0, this.buttons[p].length);
      System.arraycopy(from.sticks[p], 0, this.sticks[p], 0, this.sticks[p].length);
      System.arraycopy(from.triggers[p], 0, this.triggers[p], 0, this.triggers[p].length);
    }
  }

  /** Whether a controller sits in the player's slot. */
  public boolean connected(int player) {
    return this.connected[slot(player)];
  }

  /** Puts a controller into the player's slot, or takes it out. */
  public void setConnected(int player, boolean connected) {
    this.connected[slot(player)] = connected;
  }

  /** Whether the player's controller has the button down right now. */
  public boolean pressed(int player, GamepadButton button) {
    return this.buttons[slot(player)][button.ordinal()];
  }

  /** Sets one button of the player's controller. */
  public void setPressed(int player, GamepadButton button, boolean pressed) {
    this.buttons[slot(player)][button.ordinal()] = pressed;
  }

  /** The stick's x position, −1 (left) to 1 (right), 0 at rest. */
  public double stickX(int player, GamepadStick stick) {
    return this.sticks[slot(player)][stick.ordinal() * 2];
  }

  /** The stick's y position, −1 (down) to 1 (up), 0 at rest. */
  public double stickY(int player, GamepadStick stick) {
    return this.sticks[slot(player)][stick.ordinal() * 2 + 1];
  }

  /** Sets a stick of the player's controller; up is positive, as on the stage. */
  public void setStick(int player, GamepadStick stick, double x, double y) {
    this.sticks[slot(player)][stick.ordinal() * 2] = x;
    this.sticks[slot(player)][stick.ordinal() * 2 + 1] = y;
  }

  /** How far down the trigger is, 0 (not at all) to 1 (fully). */
  public double trigger(int player, GamepadTrigger trigger) {
    return this.triggers[slot(player)][trigger.ordinal()];
  }

  /** Sets a trigger of the player's controller, 0 (not at all) to 1 (fully). */
  public void setTrigger(int player, GamepadTrigger trigger, double value) {
    this.triggers[slot(player)][trigger.ordinal()] = value;
  }

  private static int slot(int player) {
    if (player < 1 || player > PLAYERS) {
      throw new IllegalArgumentException(
          "players are 1 to " + PLAYERS + ", not " + player);
    }
    return player - 1;
  }
}
