package org.openpatch.scratch.tiled;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.openpatch.scratch.Camera;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.extension.ExtensionContext;

/** Registers the tiled extension; found through ServiceLoader, learners never see it. */
public final class TiledExtension implements Extension {

  @Override
  public String id() {
    return "tiled";
  }

  @Override
  public String name() {
    return "Tiled maps";
  }

  @Override
  public String version() {
    return Extension.coreVersion();
  }

  /** A map layer shown in a layer of a stage. */
  record Shown(TiledMap map, MapLayer mapLayer, Layer layer) {}

  /** The shown map layers of a stage, back to front. */
  static final class ShownLayers extends CopyOnWriteArrayList<Shown> {}

  static List<Shown> shown(Stage stage) {
    return stage.extension(ShownLayers.class, ShownLayers::new);
  }

  /** Every layer of a stage draws its shown map layers, only the part the camera shows. */
  @Override
  public void install(ExtensionContext ctx) {
    for (Layer layer : Layer.values()) {
      ctx.addLayerDrawer(layer, (stage, canvas) -> {
        List<Shown> shown = shown(stage);
        if (shown.isEmpty()) {
          return;
        }
        double halfW = stage.getWidth() / 2.0, halfH = stage.getHeight() / 2.0;
        double left, right, bottom, top;
        if (layer == Layer.UI) {
          left = -halfW;
          right = halfW;
          bottom = -halfH;
          top = halfH;
        } else {
          Camera camera = stage.getCamera();
          left = camera.toWorldX(-halfW);
          right = camera.toWorldX(halfW);
          bottom = camera.toWorldY(-halfH);
          top = camera.toWorldY(halfH);
        }
        for (Shown s : shown) {
          if (s.layer() == layer) {
            s.map().draw(s.mapLayer(), canvas, left, bottom, right, top);
          }
        }
      });
    }
  }

  /** First-party extensions are released together with the core. */
  @Override
  public String requires() {
    return Extension.coreVersion();
  }
}
