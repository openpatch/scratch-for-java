package org.openpatch.scratch.internal.input;

import java.util.EnumSet;
import java.util.Set;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.MouseCode;

/**
 * What learner code can ask about the keyboard and the mouse: the game thread's copy, changed
 * only by events at the start of a batch, so it stays the same for every statement of a tick.
 */
public final class InputState {

  private final Set<KeyCode> keysDown = EnumSet.noneOf(KeyCode.class);
  private final Set<MouseCode> buttonsDown = EnumSet.noneOf(MouseCode.class);
  private double mouseX, mouseY;

  /** Applies an event to the state. */
  public void apply(InputEvent event) {
    if (event instanceof InputEvent.Key k) {
      if (k.pressed()) {
        this.keysDown.add(k.key());
      } else {
        this.keysDown.remove(k.key());
      }
    } else if (event instanceof InputEvent.Button b) {
      // held while dragging: only a release ends it (6.x dropped it on every move)
      if (b.pressed()) {
        this.buttonsDown.add(b.button());
      } else {
        this.buttonsDown.remove(b.button());
      }
      this.mouseX = b.x();
      this.mouseY = b.y();
    } else if (event instanceof InputEvent.Move m) {
      this.mouseX = m.x();
      this.mouseY = m.y();
    }
  }

  public boolean isKeyDown(KeyCode key) {
    return this.keysDown.contains(key);
  }

  public boolean isAnyKeyDown() {
    return !this.keysDown.isEmpty();
  }

  public boolean isMouseDown() {
    return !this.buttonsDown.isEmpty();
  }

  public boolean isButtonDown(MouseCode button) {
    return this.buttonsDown.contains(button);
  }

  public double getMouseX() {
    return this.mouseX;
  }

  public double getMouseY() {
    return this.mouseY;
  }

  /** Forgets everything held: a window losing focus never sees the release. */
  public void clear() {
    this.keysDown.clear();
    this.buttonsDown.clear();
  }
}
