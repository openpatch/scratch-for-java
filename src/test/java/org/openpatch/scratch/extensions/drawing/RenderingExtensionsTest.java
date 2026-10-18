package org.openpatch.scratch.extensions.drawing;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.text.BitmapText;
import org.openpatch.scratch.internal.Applet;
import processing.core.PImage;

/** Real render-loop regression checks. Run with xvfb-run -a mvn test. */
@EnabledIfEnvironmentVariable(named = "DISPLAY", matches = ".+")
class RenderingExtensionsTest {
  @TempDir Path directory;

  @Test
  void redrawsUiAndRendersDrawingAndBitmapTextInScratchCoordinates() throws Exception {
    Path log = directory.resolve("render.log");
    Process probe = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
        "-cp", System.getProperty("surefire.test.class.path", System.getProperty("java.class.path")),
        Probe.class.getName(), directory.toString()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
    try {
      assertTrue(probe.waitFor(45, TimeUnit.SECONDS), "Render probe timed out: " + Files.readString(log));
      assertEquals(0, probe.exitValue(), () -> {
        try { return Files.readString(log); } catch (Exception e) { return e.toString(); }
      });
    } finally {
      if (probe.isAlive()) probe.destroyForcibly();
    }
  }

  /** A separate process isolates Window's singleton and the native animation thread. */
  public static class Probe extends Stage {
    private static Path output;
    private final UISprite ui = new UISprite();
    private final BitmapText text;
    private Drawing expired;
    private int frames;

    public Probe() {
      this.setColor(255, 255, 255);
      String image = output.resolve("red.png").toString();
      ui.addCostume("red", image);
      ui.setPosition(-100, 60);
      this.add(ui);
      ui.stamp(Layer.UI);
      ui.setPosition(-60, 60);

      UISprite pen = new UISprite();
      pen.setPosition(40, -50);
      this.add(pen);
      pen.getPen().setColor(0, 0, 255);
      pen.getPen().setSize(6);
      pen.getPen().down();
      pen.setPosition(60, -50);
      pen.getPen().up();
      pen.hide();

      DrawingSprite drawing = new DrawingSprite() {
        @Override public void draw(Drawing canvas) {
          expired = canvas;
          canvas.clip(0, 0, 10, 10);
          canvas.rectangle(0, 0, 50, 50, new Color(0, 255, 0));
        }
      };
      drawing.setPosition(30, -20);
      drawing.setSize(200);
      this.add(drawing);
      this.add(new DrawingSprite() {
        @Override public void draw(Drawing canvas) {
          canvas.rectangle(80, -20, 10, 10, new Color(0, 0, 255));
          canvas.image(image, 0, -70, 12, 12);
          if (canvas.imageWidth(image) != 6) throw new AssertionError("Image scaling changed the cache");
        }
      });

      text = new BitmapText(output.resolve("glyph%d.png").toString());
      text.addGlyph('A', image);
      text.showText("AA");
      text.setIsUI(true);
      text.setPosition(-100, 0);
      this.add(text);
      Applet.getInstance().registerMethod("post", this);
    }

    @Override public void run() {
      frames++;
      if (frames == 8) {
        ui.setPosition(-20, 60);
        text.setPosition(-80, 0);
        text.setAlign(TextAlign.RIGHT);
        text.showText("A");
      }
      if (frames == 16) { ui.hide(); text.hide(); }
      if (frames == 24) this.eraseAll();
    }

    public void post() {
      if (frames != 6 && frames != 14 && frames != 22 && frames != 30) return;
      try {
        PImage image = Applet.getInstance().getStageImage();
        image.save(output.resolve("frame-" + frames + ".png").toString());
        pixel(image, 30, -20, 0x00ff00);
        pixel(image, 45, -20, 0xffffff);
        pixel(image, 80, -20, 0x0000ff);
        pixel(image, 0, -70, 0xff0000);
        pixel(image, -100, 60, frames < 24 ? 0xff0000 : 0xffffff);
        pixel(image, 50, -50, frames < 24 ? 0x0000ff : 0xffffff);
        pixel(image, -60, 60, frames == 6 ? 0xff0000 : 0xffffff);
        pixel(image, -20, 60, frames == 14 ? 0xff0000 : 0xffffff);
        pixel(image, -98, -2, frames == 6 ? 0xff0000 : 0xffffff);
        pixel(image, -82, -2, frames == 14 ? 0xff0000 : 0xffffff);
        try {
          expired.rectangle(0, 0, 1, 1, new Color());
          throw new AssertionError("Previous frame's drawing remained active");
        } catch (IllegalStateException expected) { }
        if (frames == 30) {
          System.out.println("UI persistence, moving/hiding, clipping, image scaling and bitmap text passed");
          // Processing owns native threads; no later test shares this child JVM.
          Runtime.getRuntime().halt(0);
        }
      } catch (Throwable failure) {
        failure.printStackTrace();
        Runtime.getRuntime().halt(1);
      }
    }

    private static void pixel(PImage image, int x, int y, int expected) {
      int actual = image.get(image.width / 2 + x, image.height / 2 - y) & 0xffffff;
      if (actual != expected) throw new AssertionError("At (" + x + "," + y + "): expected "
          + Integer.toHexString(expected) + ", got " + Integer.toHexString(actual));
    }

    public static void main(String[] args) throws Exception {
      output = Path.of(args[0]);
      BufferedImage glyph = new BufferedImage(6, 6, BufferedImage.TYPE_INT_ARGB);
      for (int y = 0; y < 6; y++) for (int x = 0; x < 6; x++) glyph.setRGB(x, y, 0xffff0000);
      ImageIO.write(glyph, "png", output.resolve("red.png").toFile());
      new Window(320, 200).setStage(new Probe());
    }
  }
}
