package org.openpatch.scratch.drawing;

import org.openpatch.scratch.Layer;
import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.extension.ExtensionContext;

/** Registers the drawing extension; found through ServiceLoader, learners never see it. */
public final class DrawingExtension implements Extension {

  @Override
  public String id() {
    return "drawing";
  }

  @Override
  public String name() {
    return "Drawing";
  }

  @Override
  public String version() {
    return Extension.coreVersion();
  }

  /**
   * Every layer gets a drawer that lets the stage's visible drawing sprites of that layer draw,
   * back to front.
   */
  @Override
  public void install(ExtensionContext ctx) {
    for (Layer layer : Layer.values()) {
      ctx.addLayerDrawer(layer, (stage, canvas) -> ctx.dispatch(stage, DrawingSprite.class, sprite -> {
        if (sprite.getLayer() == layer && sprite.isVisible()) {
          sprite.draw(new Drawing(canvas, sprite));
        }
      }));
    }
  }

  /** First-party extensions are released together with the core. */
  @Override
  public String requires() {
    return Extension.coreVersion();
  }
}
