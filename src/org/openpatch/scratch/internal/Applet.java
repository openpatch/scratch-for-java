package org.openpatch.scratch.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.text.Text;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;
import processing.sound.SoundFile;

/**
 * The Applet class represents the main application window. It is responsible
 * for loading assets,
 * managing stages, and handling mouse and keyboard events.
 */
public class Applet extends PApplet {
  private final int RENDER_HEIGHT;
  private final int RENDER_WIDTH;
  private final boolean FULLSCREEN;

  private boolean debug;
  private static Applet instance;
  private boolean isRunning = false;
  private long numberAssets;
  private long loadedAssets;
  private PImage loading;
  private final String assets;
  private Stage stage;
  private Map<String, Stage> stages;
  private int lastMillis;
  private double deltaTime;
  private boolean hasLoaded = false;
  private String loadingText = "";

  /**
   * Constructs an Applet with the specified width, height, fullscreen mode, and
   * assets path.
   *
   * @param width      the width of the window
   * @param height     the height of the window
   * @param fullscreen whether the window should be fullscreen
   * @param assets     the path to the assets directory
   */
  public Applet(int width, final int height, final boolean fullscreen, final String assets) {
    if (height == 0) {
      this.RENDER_HEIGHT = 1080;
    } else {
      this.RENDER_HEIGHT = height;
    }
    if (width == 0) {
      this.RENDER_WIDTH = 1920;
    } else {
      this.RENDER_WIDTH = width;
    }
    this.FULLSCREEN = fullscreen;
    this.assets = assets;

    this.stages = new ConcurrentHashMap<>();

    this.registerMethod("mouseEvent", this);
    this.registerMethod("keyEvent", this);
    if (Applet.instance == null) {
      Applet.instance = this;
    }
    this.runSketch();
    this.thread("loadAssets");
  }

  /**
   * Returns the instance of the application.
   *
   * @return the instance of the application
   */
  public static Applet getInstance() {
    return instance;
  }

  /**
   * Enables or disables the debug mode for the application.
   *
   * @param debug a boolean value where {@code true} enables debug mode and
   *              {@code false} disables
   *              it.
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /**
   * Checks if the application is in debug mode.
   *
   * @return true if the application is in debug mode, false otherwise.
   */
  public boolean isDebug() {
    return this.debug;
  }

  /**
   * Returns the width of the current window.
   *
   * @return the width of the window in pixels
   */
  public int getWidth() {
    return this.width;
  }

  public int getRenderWidth() {
    return this.RENDER_WIDTH;
  }

  public int getRenderHeight() {
    return this.RENDER_HEIGHT;
  }

  /**
   * Returns the height of the current window.
   *
   * @return the height of the window in pixels
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * Returns the current text size.
   *
   * @return the current text size
   */
  public double getTextSize() {
    return this.g.textSize;
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name  Name of the stage
   * @param stage A stage object
   */
  @Deprecated(since = "4.0.0")
  public void addStage(String name, Stage stage) {
    this.stages.put(name, stage);
    if (this.stage == null) {
      this.stage = stage;
    }
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name Name of the stage
   */
  @Deprecated(since = "4.0.0")
  public void switchStage(String name) {
    this.stage = this.stages.getOrDefault(name, this.stage);
  }

  /**
   * @deprecated since 4.0.0. Use setStage instead.
   * @param name Name of the stage
   */
  @Deprecated(since = "4.0.0")
  public void removeStage(String name) {
    this.stages.remove(name);
  }

  /**
   * Returns the current stage of the application.
   *
   * @return the current stage
   */
  public Stage getStage() {
    return this.stage;
  }

  /**
   * Sets the current stage of the application.
   *
   * @param stage the new stage to be set
   */
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
    if (this.FULLSCREEN) {
      this.fullScreen(P2D);
    } else {
      this.size(this.RENDER_WIDTH, this.RENDER_HEIGHT, P2D);
    }
  }

  /**
   * Returns the time since the last frame in seconds.
   *
   * @return the time since the last frame in seconds
   */
  public double getDeltaTime() {
    return deltaTime;
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
      while (this.surface.isStopped()) {
      }
      this.isRunning = true;
    }
  }

  /** Sets up the application window. */
  public void setup() {
    this.windowTitle("Scratch for Java");
    this.windowResizable(false);
    this.imageMode(PConstants.CENTER);
    this.rectMode(PConstants.CENTER);

    this.loading = this.loadImage("loading.png");
    var loadingScaleX = this.RENDER_WIDTH / 480.0;
    var loadingScaleY = this.RENDER_HEIGHT / (360.0 + 150); // normal height + padding for loading text
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

  /** Loads assets from the specified directory. */
  public void loadAssets() {
    Font.loadFont(Text.DEFAULT_FONT);
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

        var imageFiles = Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
            .map(f -> f.toString())
            .filter(f -> f.endsWith(".png") || f.endsWith(".jpg") || f.endsWith(".jpeg"))
            .collect(Collectors.toList());
        var soundFiles = Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
            .map(f -> f.toString())
            .filter(f -> f.endsWith(".mp3") || f.endsWith(".wav"))
            .collect(Collectors.toList());
        var fontFiles = Files.find(p, Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())
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

  /**
   * Handles mouse events.
   *
   * @param e the MouseEvent object
   */
  public void mouseEvent(MouseEvent e) {
    if (this.hasLoaded && this.stage != null) {
      this.stage.mouseEvent(e);
    }
  }

  /**
   * Handles keyboard events.
   *
   * @param e the KeyEvent object
   */
  public void keyEvent(KeyEvent e) {
    if (this.hasLoaded && this.stage != null) {
      this.stage.keyEvent(e);
    }
    if (e.getKeyCode() == KeyCode.VK_F11) {
      this.debug = !this.debug;
    }
  }

  public void setTextureSampling(int mode) {
    ((PGraphicsOpenGL) g).textureSampling(mode);
  }

  /** Draws the application window. */
  public void draw() {
    var currentMillis = millis();
    if (lastMillis == 0) {
      lastMillis = currentMillis;
    }
    deltaTime = (currentMillis - lastMillis) / 1000.0;
    lastMillis = currentMillis;
    if (!this.hasLoaded || this.loadingStatus() < 1) {
      this.translate(this.width / 2, this.height / 2);
      this.background(0x222222);
      this.image(this.loading, 0, 0);
      this.textAlign(CENTER);
      this.stroke(0xf58219);
      this.textFont(Font.getDefaultFont());
      this.textSize(14);
      this.text(this.loadingText, 0, this.loading.height / 2 + 20);
      this.text(round(this.loadingStatus() * 100) + "%", 0, this.loading.height / 2 + 40);
      this.textSize(14);
    } else if (this.stage != null) {
      this.stage.pre();
      this.stage.draw();
    }
  }
}
