package org.openpatch.scratch.internal.draw;

import java.util.List;

/**
 * One frame's worth of scene, in stage coordinates (origin in the centre, y up).
 *
 * @param stageId which stage this is: canvas textures belong to a stage
 * @param tick the number of ticks run when this list was made
 * @param width the stage width in stage pixels
 * @param height the stage height in stage pixels
 * @param stageColor the colour behind everything, ARGB
 * @param backdrop the current backdrop at the world origin, following the camera like the world
 *     layers (as in 6.x), or null
 * @param camera the view transform of the world layers
 * @param layers the four layers from back to front ({@code Layer.ordinal()} indexes them)
 * @param overlay speech bubbles and world texts, in front of the foreground layer and following
 *     the camera, drawn at window resolution
 * @param ui texts of the UI layer and the display band, at window resolution, in front of the
 *     UI layer's sprites
 * @param ask the ask box while a question waits for its answer, in front of the UI texts, or null
 * @param debug whether debug outlines and the frame rate are shown
 * @param stageShaders the stage's active shaders, applied in order to the world layers' picture
 * @param monitors variable monitors, above everything else
 * @param transition the transition from the previous stage, or null
 * @param overlayItems what extensions draw above everything, in stage coordinates; left out of
 *     captures
 * @param cursor the image shown instead of the mouse pointer, or null for the system's
 */
public record DrawList(
    int stageId,
    long tick,
    int width,
    int height,
    int stageColor,
    SpriteDraw backdrop,
    CameraDraw camera,
    List<LayerDraw> layers,
    List<TextDraw> overlay,
    List<TextDraw> ui,
    AskDraw ask,
    boolean debug,
    List<ShaderDraw> stageShaders,
    List<MonitorDraw> monitors,
    TransitionDraw transition,
    List<Item> overlayItems,
    CursorDraw cursor) {

  /** The same list with a transition in front of it. */
  public DrawList withTransition(TransitionDraw transition) {
    return new DrawList(this.stageId, this.tick, this.width, this.height, this.stageColor, this.backdrop, this.camera,
        this.layers, this.overlay, this.ui, this.ask, this.debug, this.stageShaders, this.monitors, transition,
        this.overlayItems, this.cursor);
  }
}
