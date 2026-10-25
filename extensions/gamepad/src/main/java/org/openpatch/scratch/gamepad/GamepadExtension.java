package org.openpatch.scratch.gamepad;

import java.util.Objects;
import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.extension.ExtensionContext;

/**
 * Registers the gamepad extension; found through ServiceLoader, learners never see it. Polls the
 * controllers once per batch of ticks (GLFW polling only, never a callback) and turns the changes
 * between ticks into the {@link GamepadListener} calls, at the same point of the tick as key
 * events.
 *
 * @ignore-in-docs
 */
public final class GamepadExtension implements Extension {

  /** Where the controllers' state comes from; swapped out in tests. */
  private static volatile GamepadSource source = new GlfwGamepadSource();

  /** The game thread's state of the controllers, read by {@link Gamepad}. */
  static final GamepadState now = new GamepadState();

  private final GamepadState before = new GamepadState();

  private static final ExtensionContext.InputKey<GamepadState> RAW =
      ExtensionContext.InputKey.of("gamepad", GamepadState::new, (from, into) -> into.copyFrom(from));

  @Override
  public String id() {
    return "gamepad";
  }

  @Override
  public String name() {
    return "Gamepad";
  }

  @Override
  public String version() {
    return Extension.coreVersion();
  }

  /** First-party extensions are released together with the core. */
  @Override
  public String requires() {
    return Extension.coreVersion();
  }

  @Override
  public void install(ExtensionContext ctx) {
    // window thread: read the controllers, nothing else
    ctx.onInputPoll(input -> source.read(input.get(RAW)));

    // game thread: compare with the last tick and tell the learner's objects
    ctx.beforeTick(stage -> {
      this.before.copyFrom(now);
      now.copyFrom(ctx.input(RAW));

      for (int player = 1; player <= GamepadState.PLAYERS; player++) {
        if (now.connected(player) && !this.before.connected(player)) {
          int p = player;
          ctx.dispatch(stage, GamepadListener.class, l -> l.whenGamepadConnected(p));
        }
        if (!now.connected(player)) {
          continue;
        }
        for (GamepadButton button : GamepadButton.values()) {
          boolean pressed = now.pressed(player, button);
          if (pressed && !this.before.pressed(player, button)) {
            ctx.dispatch(stage, GamepadListener.class, l -> l.whenGamepadButtonPressed(button));
          } else if (!pressed && this.before.pressed(player, button)) {
            ctx.dispatch(stage, GamepadListener.class, l -> l.whenGamepadButtonReleased(button));
          }
        }
      }
    });
    ctx.onExit(now::clear);
  }

  /**
   * Replaces where the controllers' state comes from, for tests and parity scripts. The new
   * source is read from the next batch of ticks on; {@code null} is not allowed.
   *
   * @param s the new source
   */
  public static void useSource(GamepadSource s) {
    source = Objects.requireNonNull(s, "a gamepad source must not be null");
  }

  /** The current source of the controllers' state. */
  public static GamepadSource getSource() {
    return source;
  }
}
