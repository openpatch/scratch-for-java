/** Controllers: up to four gamepads (Gamepad, GamepadListener). */
module org.openpatch.scratch.gamepad {
  requires transitive org.openpatch.scratch;
  requires org.lwjgl.glfw;

  exports org.openpatch.scratch.gamepad;

  provides org.openpatch.scratch.extension.Extension with org.openpatch.scratch.gamepad.GamepadExtension;
}
