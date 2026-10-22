package com.example.countdown;

import org.openpatch.scratch.Color;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.extension.ExtensionContext;

/**
 * The extension itself. Learners never see it: the library finds it through the file
 * {@code META-INF/services/org.openpatch.scratch.extension.Extension}, or a project registers it
 * with {@code Window.use(new CountdownExtension())} before creating the window.
 *
 * <p>Rules every extension follows:
 *
 * <ul>
 *   <li>Only the library's public API: nothing from {@code org.openpatch.scratch.internal}.
 *   <li>Hooks run on the game thread, like learner code; keep them short.
 *   <li>Draw through the {@code Canvas} a drawer is given; there is no OpenGL.
 *   <li>Do not call {@code getAll}, {@code find} or {@code getTouchingSprites}: their return type
 *       differs in the NRW build, so the extension would fail there. Use
 *       {@code ctx.dispatch(stage, SomeInterface.class, ...)} to reach sprites.
 * </ul>
 */
public final class CountdownExtension implements Extension {

  private static final Color TEXT = new Color(255, 255, 255);
  private static final Color BACKGROUND = new Color(0, 0, 0, 140);

  @Override
  public String id() {
    return "com.example.countdown";
  }

  @Override
  public String name() {
    return "Countdown";
  }

  @Override
  public String version() {
    return "1.0.0";
  }

  /** The oldest Scratch for Java the extension works with; a project with an older one is told. */
  @Override
  public String requires() {
    return "7.0";
  }

  @Override
  public void install(ExtensionContext ctx) {
    // count before the stage's and the sprites' run(), and tell them in the tick it runs out
    ctx.beforeTick(stage -> {
      if (Countdown.of(stage).tick()) {
        ctx.dispatch(stage, WhenTimeIsUp.class, WhenTimeIsUp::whenTimeIsUp);
      }
    });
    // the UI layer is fixed on the screen: stage coordinates, origin in the middle, y up
    ctx.addLayerDrawer(Layer.UI, (stage, canvas) -> {
      Countdown countdown = Countdown.of(stage);
      if (!countdown.isShown() || (!countdown.isRunning() && countdown.getSecondsLeft() == 0)) {
        return;
      }
      double right = stage.getWidth() / 2.0 - 8, top = stage.getHeight() / 2.0 - 8;
      canvas.drawRectangle(right - 36, top - 14, 72, 28, BACKGROUND);
      canvas.drawText(countdown.getText(), right - 64, top - 20, 18, TEXT);
    });
  }
}
