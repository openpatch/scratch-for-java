package org.openpatch.scratch.gamepad;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

/**
 * Reads the controllers through GLFW's gamepad API: polling only, never a callback. Controllers
 * without a known layout (no GLFW gamepad mapping) are skipped, and connect and disconnect are
 * detected by the extension by diffing against the previous tick.
 */
final class GlfwGamepadSource implements GamepadSource {

  /** What GLFW calls the buttons this extension names; null where GLFW has one and this has none. */
  private static final GamepadButton[] GLFW_BUTTONS = {
      GamepadButton.A, GamepadButton.B, GamepadButton.X, GamepadButton.Y,
      GamepadButton.LEFT_BUMPER, GamepadButton.RIGHT_BUMPER,
      GamepadButton.BACK, GamepadButton.START, null /* the guide button */,
      GamepadButton.LEFT_STICK, GamepadButton.RIGHT_STICK,
      GamepadButton.DPAD_UP, GamepadButton.DPAD_RIGHT, GamepadButton.DPAD_DOWN,
      GamepadButton.DPAD_LEFT};

  /** Sticks report 0 around rest; anything nearer than this counts as rest. */
  static final double DEAD_ZONE = 0.15;

  private final GLFWGamepadState raw = GLFWGamepadState.create();

  /** Fills every player slot that has a controller with a known layout, in joystick-slot order. */
  @Override
  public void read(GamepadState into) {
    into.clear();
    int player = 1;
    for (int jid = GLFW.GLFW_JOYSTICK_1;
        jid <= GLFW.GLFW_JOYSTICK_LAST && player <= GamepadState.PLAYERS; jid++) {
      if (!GLFW.glfwJoystickIsGamepad(jid) || !GLFW.glfwGetGamepadState(jid, this.raw)) {
        continue;
      }
      copy(this.raw, into, player);
      player++;
    }
  }

  /** Turns one GLFW state into the player's slot of the extension's state. */
  static void copy(GLFWGamepadState raw, GamepadState into, int player) {
    into.setConnected(player, true);
    ByteBuffer buttons = raw.buttons();
    for (int glfw = 0; glfw < GLFW_BUTTONS.length && glfw < buttons.limit(); glfw++) {
      GamepadButton button = GLFW_BUTTONS[glfw];
      if (button != null) {
        into.setPressed(player, button, buttons.get(glfw) != 0);
      }
    }
    FloatBuffer axes = raw.axes();
    into.setStick(player, GamepadStick.LEFT,
        deadZone(axes.get(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X)),
        -deadZone(axes.get(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y))); // up is positive, as on the stage
    into.setStick(player, GamepadStick.RIGHT,
        deadZone(axes.get(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X)),
        -deadZone(axes.get(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y)));
    // GLFW's triggers go from −1 (rest) to 1 (fully down)
    into.setTrigger(player, GamepadTrigger.LEFT,
        (axes.get(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) + 1) / 2);
    into.setTrigger(player, GamepadTrigger.RIGHT,
        (axes.get(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) + 1) / 2);
  }

  private static double deadZone(double value) {
    return Math.abs(value) < DEAD_ZONE ? 0 : value;
  }
}
