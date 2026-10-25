package org.openpatch.scratch.gamepad;

/**
 * A button of a gamepad, in Xbox positions. On a PlayStation controller, {@code A} is Cross,
 * {@code B} is Circle, {@code X} is Square and {@code Y} is Triangle; the bumpers are L1 and R1.
 */
public enum GamepadButton {
  /** The bottom face button (Cross on PlayStation). */
  A,
  /** The right face button (Circle). */
  B,
  /** The left face button (Square). */
  X,
  /** The top face button (Triangle). */
  Y,
  /** The bumper above the left trigger (L1). */
  LEFT_BUMPER,
  /** The bumper above the right trigger (R1). */
  RIGHT_BUMPER,
  /** The small button left of the middle (Share, View, Select). */
  BACK,
  /** The small button right of the middle (Menu, Options, Start). */
  START,
  /** Pressing the left stick into the pad (L3). */
  LEFT_STICK,
  /** Pressing the right stick into the pad (R3). */
  RIGHT_STICK,
  /** The up direction of the direction pad. */
  DPAD_UP,
  /** The right direction of the direction pad. */
  DPAD_RIGHT,
  /** The down direction of the direction pad. */
  DPAD_DOWN,
  /** The left direction of the direction pad. */
  DPAD_LEFT
}
