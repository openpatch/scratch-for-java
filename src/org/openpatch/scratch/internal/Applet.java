package org.openpatch.scratch.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.sound.SoundFile;

public class Applet extends PApplet {
  private final int INITIAL_HEIGHT;
  private final int INITIAL_WIDTH;

  private boolean debug;
  private static Applet instance;
  private boolean isRunning = false;
  private long numberAssets;
  private long loadedAssets;
  private PImage loading;
  private final String assets;
  private Stage stage;

  private boolean hasLoaded = false;
  private String loadingText = "";

  public Applet(int width, final int height, final String assets) {
    this.INITIAL_HEIGHT = height;
    this.INITIAL_WIDTH = width;
    this.assets = assets;

    this.registerMethod("pre", this);
    this.registerMethod("mouseEvent", this);
    this.registerMethod("keyEvent", this);
    if (Applet.instance == null) {
      Applet.instance = this;
    }
    this.runSketch();
    this.thread("loadAssets");
  }

  public static Applet getInstance() {
    return instance;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public boolean isDebug() {
    return this.debug;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Use this method to call size().
   *
   * @see PApplet#fullScreen()
   * @see PApplet#setup()
   * @see PApplet#size(int,int)
   * @see PApplet#smooth()
   */
  public void settings() {
    this.size(this.INITIAL_WIDTH, this.INITIAL_HEIGHT, P2D);
  }

  /** Pauses the sketch. */
  public void pauseSketch() {
    this.noLoop();
  }

  /** Resumes the sketch. */
  public void resumeSketch() {
    this.loop();
  }

  /** Forces a redraw, even if the sketch is paused. */
  public void redrawSketch() {
    this.redraw();
  }

  public void runSketch() {
    if (!this.isRunning) {
      super.runSketch();
      while (this.surface.isStopped()) {}
      this.isRunning = true;
    }
  }

  public void setup() {
    this.windowTitle("Scratch for Java");
    this.imageMode(PConstants.CENTER);
    this.rectMode(PConstants.CENTER);
    this.loading = this.loadImage("loading.png");
    var loadingScaleX = this.INITIAL_WIDTH / 480.0;
    var loadingScaleY =
        this.INITIAL_HEIGHT / (360.0 + 150); // normal height + padding for loading text
    var scale = Math.min(1, Math.min(loadingScaleX, loadingScaleY));
    this.loading.resize((int) (this.loading.width * scale), (int) (this.loading.height * scale));
  }

  private void setLoadingText(String type, final String path) {
    this.loadingText = "Loading " + type + ": ";
    if (path.length() > 40) {
      this.loadingText += "..." + path.substring(path.length() - 40);
    } else {
      this.loadingText += path + " ".repeat(40 - path.length());
    }
  }

  public void loadAssets() {
    Font.loadFont(Font.defaultFontPath);
    if (this.assets != null) {
      try {
        this.loadingText = "Finding files...";
        var sr = ClassLoader.getSystemResource(this.assets);
        var p = Paths.get(this.assets);
        if (sr != null) {
          try {
            p = Path.of(sr.toURI());
          } catch (FileSystemNotFoundException e) {
            p = Paths.get(this.assets);
          }
        }

        var imageFiles =
            Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
                .map(f -> f.toString())
                .filter(f -> f.endsWith(".png") || f.endsWith(".jpg") || f.endsWith(".jpeg"))
                .collect(Collectors.toList());
        var soundFiles =
            Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
                .map(f -> f.toString())
                .filter(f -> f.endsWith(".mp3") || f.endsWith(".wav"))
                .collect(Collectors.toList());
        var fontFiles =
            Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
                .map(f -> f.toString())
                .filter(f -> f.endsWith(".ttf") || f.endsWith(".otf"))
                .collect(Collectors.toList());
        this.numberAssets += imageFiles.size();
        this.numberAssets += soundFiles.size();
        this.numberAssets += fontFiles.size();
        for (var file : imageFiles) {
          this.setLoadingText("Image", file);
          Image.loadImage(file);
          this.loadedAssets += 1;
        }
        for (var file : fontFiles) {
          this.setLoadingText("Font", file);
          Font.loadFont(file);
          this.loadedAssets += 1;
        }
        for (var file : soundFiles) {
          this.setLoadingText("Sound", file);
          new SoundFile(this, file, true);
          this.loadedAssets += 1;
        }
      } catch (IOException | URISyntaxException e) {
      }
    }
    this.hasLoaded = true;
  }

  private float loadingStatus() {
    return this.numberAssets > 0 ? this.loadedAssets / (float) this.numberAssets : 1;
  }

  public void pre() {
    if (this.hasLoaded && this.stage != null) {
      this.stage.pre();
    }
  }

  public void mouseEvent(MouseEvent e) {
    if (this.hasLoaded && this.stage != null) {
      this.stage.mouseEvent(e);
    }
  }

  public void keyEvent(KeyEvent e) {
    if (this.hasLoaded && this.stage != null) {
      this.stage.keyEvent(e);
    }
    if (e.getKeyCode() == KeyCode.VK_F11) {
      this.debug = !this.debug;
    }
  }

  public void draw() {
    if (!this.hasLoaded || this.loadingStatus() < 1) {
      this.background(0x222222);
      this.image(this.loading, this.width / 2, this.height / 2);
      this.textAlign(CENTER);
      this.stroke(0xf58219);
      this.textFont(Font.getDefaultFont());
      this.textSize(14);
      this.text(this.loadingText, this.width / 2, this.height / 2 + this.loading.height / 2 + 20);
      this.text(
          round(this.loadingStatus() * 100) + "%",
          this.width / 2,
          this.height / 2 + this.loading.height / 2 + 40);
      this.textSize(14);
    } else if (this.stage != null) {
      this.stage.draw();
    }
  }
}
