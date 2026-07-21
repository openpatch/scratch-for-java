package org.openpatch.scratch.internal;

import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * The four things the render loop needs to do to a stage.
 *
 * <p>
 * A stage hands one of these to {@link Applet} when it is created. That way the
 * methods behind them can stay private on the stage itself, instead of having to
 * be public only so that a class in another package can reach them.
 *
 * <p>
 * Nothing outside the library should implement or call this.
 */
public interface StageHooks {

  /** Runs once before the stage is drawn. */
  void pre();

  /**
   * Draws the stage.
   *
   * @param buffer where to draw
   */
  void draw(PGraphics buffer);

  /**
   * Passes a key press or release to the stage.
   *
   * @param e the key event
   */
  void keyEvent(KeyEvent e);

  /**
   * Passes a mouse event to the stage.
   *
   * @param e the mouse event
   */
  void mouseEvent(MouseEvent e);
}
