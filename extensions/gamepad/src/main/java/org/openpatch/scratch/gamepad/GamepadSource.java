package org.openpatch.scratch.gamepad;

/**
 * Where the controllers' state comes from. The extension polls GLFW by default; tests and parity
 * scripts swap in made-up state with {@link GamepadExtension#useSource(GamepadSource)}.
 *
 * @ignore-in-docs
 */
@FunctionalInterface
public interface GamepadSource {

  /**
   * Writes the current state of every controller into {@code into}, which starts out empty: poll
   * everything, then fill. Called once per batch of ticks, on the window thread.
   *
   * @param into the state to fill, reused between polls
   */
  void read(GamepadState into);
}
