package org.openpatch.scratch.internal.input;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.MouseCode;

/**
 * An input event, queued by the window thread (or a test) and handed out on the game thread at
 * the start of the next batch of ticks. Coordinates are stage coordinates (origin in the centre,
 * y up), already mapped through the letterbox; the camera is applied later.
 */
public sealed interface InputEvent {

  /** A key went down or up. Auto-repeat never arrives here. */
  record Key(KeyCode key, boolean pressed) implements InputEvent {}

  /** A character was typed (umlauts and dead keys included). */
  record Typed(int codepoint) implements InputEvent {}

  /**
   * A held key repeating (GLFW_REPEAT). Not a key event for learners, who get one
   * {@code whenKeyPressed} per press as Processing had it; only the text box of {@code ask()}
   * acts on it, so holding Backspace keeps deleting.
   */
  record Repeat(KeyCode key) implements InputEvent {}

  /** A mouse button went down or up at a point. */
  record Button(MouseCode button, boolean pressed, double x, double y) implements InputEvent {}

  /** The mouse moved to a point. */
  record Move(double x, double y) implements InputEvent {}

  /** The wheel turned; positive is towards the user (down), in notches. */
  record Wheel(double steps) implements InputEvent {}
}
