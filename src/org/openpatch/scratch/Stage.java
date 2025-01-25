package org.openpatch.scratch;

import java.awt.Polygon;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.openpatch.scratch.extensions.camera.Camera;
import org.openpatch.scratch.extensions.color.Color;
import org.openpatch.scratch.extensions.hitbox.Hitbox;
import org.openpatch.scratch.extensions.math.Vector2;
import org.openpatch.scratch.extensions.pen.Pen;
import org.openpatch.scratch.extensions.shader.Shader;
import org.openpatch.scratch.extensions.text.Text;
import org.openpatch.scratch.extensions.text.TextStyle;
import org.openpatch.scratch.extensions.timer.Timer;
import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.Font;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Sound;
import org.openpatch.scratch.internal.Stamp;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;

/**
 * The Stage class represents a stage where various elements such as sprites,
 * texts, pens, and
 * backdrops can be added and manipulated. It provides methods to manage these
 * elements, handle
 * events, and control the stage's appearance and behavior.
 */
public class Stage {

  private final List<Image> backdrops = new CopyOnWriteArrayList<>();
  private Color color = new Color();
  private int currentBackdrop = 0;
  private final List<Sound> sounds = new CopyOnWriteArrayList<>();
  private int currentShader = 0;
  private List<Shader> shaders = new CopyOnWriteArrayList<>();

  private PGraphics shaderBuffer;
  private PGraphics mainBuffer;

  private PGraphics backdropBuffer;

  public Queue<Stamp> backgroundStamps;
  private PGraphics backgroundBuffer;
  private boolean eraseBackgroundBuffer;

  public Queue<Stamp> foregroundStamps;
  private PGraphics foregroundBuffer;
  private boolean eraseForegroundBuffer;

  public Queue<Stamp> uiStamps;
  private PGraphics uiBuffer;
  private boolean eraseUIBuffer;

  private PGraphics debugBuffer;

  private String cursor;
  private int cursorActiveSpotX;
  private int cursorActiveSpotY;
  private final Text display;
  private final AbstractMap<String, Timer> timer;
  List<Text> texts;
  List<Pen> pens;
  List<Sprite> sprites;
  private double mouseX;
  private double mouseY;
  private boolean mouseDown;
  private final AbstractMap<Integer, Boolean> keyCodePressed = new ConcurrentHashMap<>();
  private Comparator<? super Sprite> sorter;

  Hitbox leftBorder;
  Hitbox rightBorder;
  Hitbox topBorder;
  Hitbox bottomBorder;

  private Camera camera;

  /**
   * Constructs a new Stage with default dimensions. The default width is 480
   * pixels and the default
   * height is 360 pixels.
   */
  public Stage() {
    this(480, 360);
  }

  /**
   * Constructs a new Stage with the specified width and height.
   *
   * @param width  the width of the stage
   * @param height the height of the stage
   */
  public Stage(int width, final int height) {
    this(width, height, null);
  }

  /**
   * Constructs a new Stage with the specified width, height, and assets path.
   *
   * @param width  the width of the stage
   * @param height the height of the stage
   * @param assets the path to the assets directory
   */
  public Stage(int width, int height, String assets) {
    this(width, height, false, assets);
  }

  /**
   * Constructs a new Stage.
   *
   * @param fullScreen a boolean indicating whether the stage should be in full
   *                   screen mode.
   */
  public Stage(boolean fullScreen) {
    this(fullScreen, null);
  }

  /**
   * Constructs a new Stage with the specified fullscreen mode and assets path.
   *
   * @param fullScreen a boolean indicating whether the stage should be in
   *                   fullscreen mode
   * @param assets     the path to the assets directory
   */
  public Stage(boolean fullScreen, String assets) {
    this(0, 0, fullScreen, assets);
  }

  /**
   * Constructs a new Stage with the specified width and height.
   *
   * @param width      the width of the stage
   * @param height     the height of the stage
   * @param fullScreen whether the stage should be in full screen mode
   */
  public Stage(boolean fullScreen, int width, int height) {
    this(width, height, fullScreen, null);
  }

  /**
   * Constructs a new Stage with the specified parameters.
   *
   * @param fullScreen whether the stage should be in full screen mode
   * @param width      the width of the stage
   * @param height     the height of the stage
   * @param assets     the path to the assets directory
   */
  public Stage(boolean fullScreen, int width, int height, String assets) {
    this(width, height, fullScreen, assets);
  }

  /**
   * Constructs a new Stage with the specified parameters.
   *
   * @param width      the width of the stage
   * @param height     the height of the stage
   * @param fullScreen whether the stage should be in full screen mode
   * @param assets     the path to the assets directory
   */
  private Stage(int width, final int height, boolean fullScreen, String assets) {
    this.cursor = null;
    this.camera = new Camera();
    this.texts = new CopyOnWriteArrayList<>();
    this.pens = new CopyOnWriteArrayList<>();
    this.sprites = new CopyOnWriteArrayList<>();
    this.shaders = new CopyOnWriteArrayList<>();
    this.backgroundStamps = new ConcurrentLinkedQueue<>();
    this.foregroundStamps = new ConcurrentLinkedQueue<>();
    this.uiStamps = new ConcurrentLinkedQueue<>();
    this.timer = new ConcurrentHashMap<>();
    if (Window.getInstance() == null) {
      if (fullScreen) {
        new Window(fullScreen, width, height, assets);
      } else {
        new Window(width, height, assets);
      }
      Applet a = Applet.getInstance();
      a.setStage(this);
    }
    Applet applet = Applet.getInstance();

    this.shaderBuffer = applet.createGraphics(
        applet.getRenderWidth(), applet.getRenderHeight(), applet.sketchRenderer());
    this.mainBuffer = applet.createGraphics(
        applet.getRenderWidth(), applet.getRenderHeight(), applet.sketchRenderer());
    this.backdropBuffer = applet.createGraphics(
        applet.getRenderWidth(), applet.getRenderHeight(), applet.sketchRenderer());
    this.backgroundBuffer = applet.createGraphics(
        applet.getRenderWidth(), applet.getRenderHeight(), applet.sketchRenderer());
    this.foregroundBuffer = applet.createGraphics(
        applet.getRenderWidth(), applet.getRenderHeight(), applet.sketchRenderer());
    this.uiBuffer = applet.createGraphics(
        applet.getRenderWidth(), applet.getRenderHeight(), applet.sketchRenderer());
    this.debugBuffer = applet.createGraphics(
        applet.getRenderWidth(), applet.getRenderHeight(), applet.sketchRenderer());
    ((PGraphicsOpenGL) this.shaderBuffer).textureSampling(Window.TEXTURE_SAMPLING_MODE);
    ((PGraphicsOpenGL) this.mainBuffer).textureSampling(Window.TEXTURE_SAMPLING_MODE);
    ((PGraphicsOpenGL) this.backgroundBuffer).textureSampling(Window.TEXTURE_SAMPLING_MODE);
    ((PGraphicsOpenGL) this.debugBuffer).textureSampling(Window.TEXTURE_SAMPLING_MODE);
    ((PGraphicsOpenGL) this.backdropBuffer).textureSampling(Window.TEXTURE_SAMPLING_MODE);
    ((PGraphicsOpenGL) this.uiBuffer).textureSampling(Window.TEXTURE_SAMPLING_MODE);
    ((PGraphicsOpenGL) this.foregroundBuffer).textureSampling(Window.TEXTURE_SAMPLING_MODE);
    if (Window.TEXTURE_SAMPLING_MODE == 2) {
      this.shaderBuffer.noSmooth();
      this.mainBuffer.noSmooth();
      this.backgroundBuffer.noSmooth();
      this.debugBuffer.noSmooth();
      this.backdropBuffer.noSmooth();
      this.uiBuffer.noSmooth();
      this.foregroundBuffer.noSmooth();
    }

    this.timer.put("default", new Timer());
    this.display = new Text(
        null,
        -applet.getRenderWidth() / 2,
        -applet.getRenderHeight() / 2,
        applet.getRenderWidth(),
        TextStyle.BOX);
    this.display.addedToStage(this);

    var p = new Polygon();
    p.addPoint(-this.getWidth() / 2, this.getHeight() / 2);
    p.addPoint(-this.getWidth() / 2 - 5, this.getHeight() / 2);
    p.addPoint(-this.getWidth() / 2 - 5, -this.getHeight() / 2);
    p.addPoint(-this.getWidth() / 2, -this.getHeight() / 2);
    this.leftBorder = new Hitbox(p);

    p = new Polygon();
    p.addPoint(this.getWidth() / 2, this.getHeight() / 2);
    p.addPoint(this.getWidth() / 2 + 5, this.getHeight() / 2);
    p.addPoint(this.getWidth() / 2 + 5, -this.getHeight() / 2);
    p.addPoint(this.getWidth() / 2, -this.getHeight() / 2);
    this.rightBorder = new Hitbox(p);

    p = new Polygon();
    p.addPoint(-this.getWidth() / 2, -this.getHeight() / 2);
    p.addPoint(-this.getWidth() / 2, -this.getHeight() / 2 - 5);
    p.addPoint(this.getWidth() / 2, -this.getHeight() / 2 - 5);
    p.addPoint(this.getWidth() / 2, -this.getHeight() / 2);
    this.topBorder = new Hitbox(p);

    p = new Polygon();
    p.addPoint(-this.getWidth() / 2, this.getHeight() / 2);
    p.addPoint(-this.getWidth() / 2, this.getHeight() / 2 + 5);
    p.addPoint(this.getWidth() / 2, this.getHeight() / 2 + 5);
    p.addPoint(this.getWidth() / 2, this.getHeight() / 2);
    this.bottomBorder = new Hitbox(p);
  }

  /**
   * Enables or disables the debug mode for the application.
   *
   * @param debug a boolean value where {@code true} enables debug mode and
   *              {@code false} disables
   *              it.
   */
  public void setDebug(boolean debug) {
    Applet.getInstance().setDebug(debug);
  }

  /**
   * Checks if the application is in debug mode.
   *
   * @return true if the application is in debug mode, false otherwise.
   */
  public boolean isDebug() {
    return Applet.getInstance().isDebug();
  }

  /**
   * Add a sprite object to the stage
   *
   * @param sprite a sprite
   */
  public void add(Sprite sprite) {
    this.sprites.add(sprite);
    sprite.addedToStage(this);
  }

  /**
   * Add a text object to the stage
   *
   * @param text a text
   */
  public void add(Text text) {
    this.texts.add(text);
    text.addedToStage(this);
  }

  /**
   * Adds a Pen object to the stage.
   *
   * @param pen the Pen object to be added to the stage
   */
  public void add(Pen pen) {
    this.pens.add(pen);
    pen.addedToStage(this);
  }

  /**
   * <<<<<<< HEAD
   * Adds a new shader to the sprite. If a shader with the received name already
   * exists, this method
   * =======
   * Sets the texture sampling mode Point sampling: both magnification and
   * minification filtering
   * are set to nearest. Linear sampling: magnification filtering is nearest,
   * minification set to
   * linear Bilinear sampling: both magnification filtering is set to linear and
   * minification either
   * to linear-mipmap-nearest (linear interpolation is used within a mipmap, but
   * not between
   * different mipmaps). Trilinear sampling: magnification filtering set to
   * linear, minification to
   * linear-mipmap-linear, which offers the best mipmap quality since linear
   * interpolation to
   * compute the value in each of two maps and then interpolates linearly between
   * these two values.
   *
   * @param mode the texture sampling mode. 2: Point Sampling. 3: Linear. 4:
   *             Bilinear. 5: Trilinear.
   */
  public void setTextureSampling(int mode) {
    if (mode < 2 || mode > 5)
      return;
    Applet.getInstance().setTextureSampling(mode);
    ((PGraphicsOpenGL) this.shaderBuffer).textureSampling(mode);
    ((PGraphicsOpenGL) this.mainBuffer).textureSampling(mode);
    ((PGraphicsOpenGL) this.backgroundBuffer).textureSampling(mode);
    ((PGraphicsOpenGL) this.debugBuffer).textureSampling(mode);
    ((PGraphicsOpenGL) this.backdropBuffer).textureSampling(mode);
    ((PGraphicsOpenGL) this.uiBuffer).textureSampling(mode);
    ((PGraphicsOpenGL) this.foregroundBuffer).textureSampling(mode);
  }

  /**
   * Adds a new shader to the sprite. If a shader with the received name already
   * exists, this method
   * >>>>>>> d7b76036d7d2175245fa8de40d42d972914b35f6
   * does nothing.
   *
   * @param name
   * @param shaderPath
   * @return the shader
   */
  public Shader addShader(String name, final String shaderPath) {
    for (Shader shader : this.shaders) {
      if (shader.getName().equals(name)) {
        return shader;
      }
    }

    Shader shader = new Shader(name, shaderPath);
    this.shaders.add(shader);
    return shader;
  }

  /**
   * Switch to a shader by name.
   *
   * @param name the name of a shader
   */
  public void switchShader(String name) {
    for (int i = 0; i < this.shaders.size(); i++) {
      Shader shader = this.shaders.get(i);
      if (shader.getName().equals(name)) {
        this.currentShader = i;
        return;
      }
    }
  }

  /**
   * Switch to a shader by index.
   *
   * @param index the index of a shader
   */
  public void switchShader(double index) {
    this.currentShader = (int) index % this.shaders.size();
  }

  public void resetShader() {
    this.currentShader = -1;
  }

  /**
   * Retrieves a shader by name.
   *
   * @param name the name of a shader
   * @return the shader with the specified name, or null if no shader with that
   *         name exists
   */
  public Shader getShader(String name) {
    for (Shader shader : this.shaders) {
      if (shader.getName().equals(name)) {
        return shader;
      }
    }
    return null;
  }

  /** Sets the next shader as the current shader. */
  public void nextShader() {
    this.currentShader = (this.currentShader + 1) % this.shaders.size();
  }

  /**
   * Retrieves the name of the current shader.
   *
   * @return the name of the current shader, or null if no shaders exist
   */
  public String getCurrentShaderName() {
    if (this.shaders.size() == 0 || this.currentShader == -1)
      return null;

    return this.shaders.get(this.currentShader).getName();
  }

  /**
   * Retrieves the index of the current shader.
   *
   * @return the index of the current shader
   */
  public int getCurrentShaderIndex() {
    return this.currentShader;
  }

  /**
   * Retrieves the current shader.
   *
   * @return the current shader, or null if no shaders exist
   */
  public Shader getCurrentShader() {
    if (this.shaders.size() == 0 || this.currentShader == -1)
      return null;

    return this.shaders.get(this.currentShader);
  }

  /**
   * Moves the specified sprite backwards by a given number of layers in the
   * sprite list. If the
   * resulting position is less than zero, the sprite is moved to the first
   * position. If the
   * resulting position is greater than the last index, the sprite is moved to the
   * last position.
   *
   * @param sprite the sprite to be moved backwards in the layer order
   * @param number the number of layers to move the sprite backwards
   */
  public void goLayersBackwards(Sprite sprite, int number) {
    int index = this.sprites.indexOf(sprite);
    if (index == -1)
      return;
    int newIndex = index - number;
    if (newIndex < 0)
      newIndex = 0;
    newIndex = Math.min(newIndex, this.sprites.size() - 1);
    this.sprites.remove(index);
    this.sprites.add(newIndex, sprite);
  }

  /**
   * Moves the specified sprite forward by a given number of layers in the sprite
   * list. If the
   * resulting position is out of bounds, it will be adjusted to the nearest valid
   * position.
   *
   * @param sprite the sprite to be moved forward in the layer order
   * @param number the number of layers to move the sprite forward
   */
  public void goLayersForwards(Sprite sprite, int number) {
    int index = this.sprites.indexOf(sprite);
    if (index == -1)
      return;
    int newIndex = index + number;
    if (newIndex < 0)
      newIndex = 0;
    newIndex = Math.min(newIndex, this.sprites.size() - 1);
    this.sprites.remove(index);
    this.sprites.add(newIndex, sprite);
  }

  /**
   * Moves the specified sprite to the front layer.
   *
   * @param sprite the sprite to be moved to the front layer
   */
  public void goToFrontLayer(Sprite sprite) {
    this.sprites.remove(sprite);
    this.sprites.add(sprite);
  }

  /**
   * Moves the specified sprite to the back layer of the stage.
   *
   * @param sprite the sprite to be moved to the back layer
   */
  public void goToBackLayer(Sprite sprite) {
    this.sprites.remove(sprite);
    this.sprites.add(0, sprite);
  }

  /**
   * Moves the specified sprite to the UI layer by removing it from the current
   * list of sprites.
   *
   * @param sprite the sprite to be moved to the UI layer
   */
  public void goToUILayer(Sprite sprite) {
    this.sprites.remove(sprite);
  }

  /**
   * Sets a custom sorter for the sprites. Use enableYSort() to enable the sorting
   * of sprites using
   * the y-coordinates. This overwrites goToBackLayer(), goToFrontLayer(),
   * goLayersBackwards() and
   * goLayersForwards().
   *
   * @see #enableYSort()
   * @param sorter the comparator used to sort the sprites
   */
  public void setSorter(Comparator<? super Sprite> sorter) {
    this.sorter = sorter;
  }

  /**
   * Enables the sorting of sprites using y-sorting. This means that sprites with
   * a lower
   * y-coordinate will be drawn on top of sprites with a higher y-coordinate. This
   * sorting respects
   * the height of the sprites. This overwrites goToBackLayer(), goToFrontLayer(),
   * goLayersBackwards() and goLayersForwards().
   */
  public void enableYSort() {
    this.sorter = (s1, s2) -> (int) ((s2.getY() - s2.getHeight() / 2) - (s1.getY() - s1.getHeight() / 2));
  }

  /** Disables the sorting of sprites. */
  public void disableSort() {
    this.sorter = null;
  }

  /**
   * Checks if the sorting of sprites is enabled.
   *
   * @return true if sorting is enabled, false otherwise
   */
  public boolean isSortEnabled() {
    return this.sorter != null;
  }

  /**
   * Retrieves a list of all sprites in the current stage.
   *
   * @return a list containing all sp
   */
  public List<Sprite> getAll() {
    return new CopyOnWriteArrayList<>(this.sprites);
  }

  /**
   * Removes the specified sprite from the stage.
   *
   * @param sprite the sprite to be removed
   */
  public void remove(Sprite sprite) {
    this.sprites.remove(sprite);
    sprite.removedFromStage(this);
  }

  /**
   * Removes the specified pen from the stage.
   *
   * @param pen the pen to be removed
   */
  public void remove(Pen pen) {
    this.pens.remove(pen);
    pen.removedFromStage(this);
  }

  /**
   * Removes the specified text from the stage.
   *
   * @param text the text to be removed
   */
  public void remove(Text text) {
    this.texts.remove(text);
    text.removedFromStage(this);
  }

  /** Removes all elements from the stage. */
  public void removeAll() {
    this.removeAllSprites();
    this.removeAllTexts();
    this.removeAllPens();
  }

  /** Removes all sprites from the stage. */
  public void removeAllSprites() {
    for (Sprite sprite : this.sprites) {
      sprite.removedFromStage(this);
    }
    this.sprites.clear();
  }

  /** Removes all texts from the stage. */
  public void removeAllTexts() {
    for (Text text : this.texts) {
      text.removedFromStage(this);
    }
    this.texts.clear();
  }

  /** Removes all pens from the stage. */
  public void removeAllPens() {
    for (Pen pen : this.pens) {
      pen.removedFromStage(this);
    }
    this.pens.clear();
  }

  /**
   * Removes all sprites of the specified class from the stage.
   *
   * @param c the class of the sprites to remove
   */
  public void remove(Class<? extends Sprite> c) {
    for (Sprite sprite : this.sprites) {
      if (c.isInstance(sprite)) {
        sprite.removedFromStage(this);
      }
    }
    this.sprites.removeIf(c::isInstance);
  }

  /**
   * Find sprites of a given class.
   *
   * @param c Class
   */
  public <T extends Sprite> List<T> find(Class<T> c) {
    return this.sprites.stream().filter(c::isInstance).map(c::cast).collect(Collectors.toList());
  }

  /**
   * Find sprites of a given class.
   *
   * @param c Class
   */
  public <T extends Sprite> List<T> findSpritesOf(Class<T> c) {
    return this.find(c);
  }

  /**
   * Find texts of a given class.
   *
   * @param c Class
   */
  public <T extends Text> List<T> findTextsOf(Class<T> c) {
    return this.texts.stream().filter(c::isInstance).map(c::cast).collect(Collectors.toList());
  }

  /**
   * Find texts of a given class.
   *
   * @param c Class
   */
  public <T extends Pen> List<T> findPensOf(Class<T> c) {
    return this.pens.stream().filter(c::isInstance).map(c::cast).collect(Collectors.toList());
  }

  /**
   * Returns the number of sprites of the specified class.
   *
   * @param c the class of the sprites to count
   * @return the number of sprites of the specified class
   */
  public <T extends Sprite> long count(Class<T> c) {
    return this.sprites.stream().filter(c::isInstance).count();
  }

  /**
   * Returns the number of sprites in the stage.
   *
   * @return the number of sprites
   */
  public long countSprites() {
    return this.sprites.size();
  }

  /**
   * Returns the number of sprites of the specified class.
   *
   * @param c the class of the sprites to count
   * @return the number of sprites of the specified class
   */
  public <T extends Sprite> long countSpritesOf(Class<T> c) {
    return this.count(c);
  }

  /**
   * Returns the number of texts in the stage.
   *
   * @return the number of texts
   */
  public long countTexts() {
    return this.texts.size();
  }

  /**
   * Returns the number of texts of the specified class.
   *
   * @param c the class of the texts to count
   */
  public <T extends Text> long countTextsOf(Class<T> c) {
    return this.texts.stream().filter(c::isInstance).count();
  }

  /**
   * Returns the number of pens
   *
   * @return the number of pens
   */
  public long countPens() {
    return this.pens.size();
  }

  /**
   * Returns the number of pens of the specified class.
   *
   * @param c the class of the pens to count
   */
  public <T extends Pen> long countPensOf(Class<T> c) {
    return this.pens.stream().filter(c::isInstance).count();
  }

  /**
   * Add a backdrop to the stage. If a backdrop with the received name already
   * exists do nothing.
   *
   * @param name      a unique name
   * @param imagePath a image path
   * @param stretch   stretch image to window size
   */
  public void addBackdrop(String name, final String imagePath, boolean stretch) {
    for (Image backdrop : this.backdrops) {
      if (backdrop.getName().equals(name)) {
        return;
      }
    }
    Image backdrop = new Image(name, imagePath);
    if (stretch) {
      backdrop.setSize(this.getWidth(), this.getHeight());
    }
    this.backdrops.add(backdrop);
  }

  /**
   * Add a backdrop to the stage. If a backdrop with the received name already
   * exists do nothing.
   *
   * @param name      a unique name
   * @param imagePath a image path
   */
  public void addBackdrop(String name, final String imagePath) {
    this.addBackdrop(name, imagePath, false);
  }

  /**
   * Remove a backdrop from the stage.
   *
   * @param name of the backdrop
   */
  public void removeBackdrop(String name) {
    for (int i = 0; i < this.backdrops.size(); i++) {
      Image backdrop = this.backdrops.get(i);
      if (backdrop.getName().equals(name)) {
        this.backdrops.remove(i);
        return;
      }
    }
  }

  /**
   * Switch to a backdrop by name.
   *
   * @param name the name of a backdrop
   */
  public void switchBackdrop(String name) {
    for (int i = 0; i < this.backdrops.size(); i++) {
      Image backdrop = this.backdrops.get(i);
      if (backdrop.getName().equals(name)) {
        this.currentBackdrop = i;
        this.emitBackdropSwitch();
        return;
      }
    }
  }

  private void emitBackdropSwitch() {
    Image backdrop = this.backdrops.get(this.currentBackdrop);
    String name = backdrop.getName();
    this.sprites.stream().forEach(s -> s.whenBackdropSwitches(name));
    this.whenBackdropSwitches(name);
  }

  /**
   * This method is called when the backdrop switches to the specified name.
   * Override this method to
   * add custom behavior.
   *
   * @param name the name of the backdrop to switch to
   */
  public void whenBackdropSwitches(String name) {
  }

  /** Switch to the next backdrop. */
  public void nextBackdrop() {
    this.currentBackdrop = (this.currentBackdrop + 1) % this.backdrops.size();
    this.emitBackdropSwitch();
  }

  /** Switch to the previous backdrop. */
  public void previousBackdrop() {
    this.currentBackdrop = (this.currentBackdrop - 1) % this.backdrops.size();
    this.emitBackdropSwitch();
  }

  /** Switch to a random backdrop. */
  public void randomBackdrop() {
    int size = this.backdrops.size();
    this.currentBackdrop = this.pickRandom(0, size - 1) % size;
    this.emitBackdropSwitch();
  }

  /**
   * Returns the current backdrop name
   *
   * @return a backdrop name
   */
  public String getCurrentBackdropName() {
    return this.backdrops.get(this.currentBackdrop).getName();
  }

  /**
   * Returns the current backdrop index
   *
   * @return a backdrop index
   */
  public int getCurrentBackdropIndex() {
    return this.currentBackdrop;
  }

  /** Erases all lines on the pen layer. */
  public void eraseAll() {
    this.eraseBackgroundBuffer = true;
    this.eraseForegroundBuffer = true;
    this.eraseUIBuffer = true;
  }

  /** When this method is called, the background buffer will be erased. */
  public void eraseBackground() {
    this.eraseBackgroundBuffer = true;
  }

  /** When this method is called, the foreground buffer will be erased. */
  public void eraseForeground() {
    this.eraseForegroundBuffer = true;
  }

  public int[] getForegroundPixels() {
    this.foregroundBuffer.loadPixels();
    return this.foregroundBuffer.pixels;
  }

  public int[] getBackgroundPixels() {
    this.backgroundBuffer.loadPixels();
    return this.backgroundBuffer.pixels;
  }

  public int[] getPixels() {
    this.mainBuffer.loadPixels();
    return this.mainBuffer.pixels;
  }

  /**
   * This method marks the UI buffer to be erased, which will be processed in the
   * next update cycle.
   */
  public void eraseUI() {
    this.eraseUIBuffer = true;
  }

  /**
   * Add a sound to the stage. If a sound with the received name already exists do
   * nothing.
   *
   * @param name      a unique name
   * @param soundPath a sound path
   */
  public void addSound(String name, final String soundPath) {
    for (Sound sound : this.sounds) {
      if (sound.getName().equals(name)) {
        return;
      }
    }

    Sound sound = new Sound(name, soundPath);
    this.sounds.add(sound);
  }

  /**
   * Remove a sound from the stage.
   *
   * @param name the sound name
   */
  public void removeSound(String name) {
    for (int i = 0; i < this.sounds.size(); i++) {
      Sound sound = this.sounds.get(i);
      if (sound.getName().equals(name)) {
        this.sounds.remove(i);
        return;
      }
    }
  }

  /**
   * Plays a sound.
   *
   * @param name the sound name
   */
  public void playSound(String name) {
    for (Sound sound : this.sounds) {
      if (sound.getName().equals(name) && !sound.isPlaying()) {
        sound.play();
      }
    }
  }

  /** Stops the playing of all sounds of the stage. */
  public void stopAllSounds() {
    for (Sound sound : this.sounds) {
      sound.stop();
    }
  }

  /**
   * Stops the playing of the sound with the given name
   *
   * @param name Name of the sound
   */
  public void stopSound(String name) {
    for (Sound sound : this.sounds) {
      if (sound.getName().equals(name)) {
        sound.stop();
        break;
      }
    }
  }

  /**
   * Returns true if the sound if playing
   *
   * @return playing
   */
  public boolean isSoundPlaying(String name) {
    for (Sound sound : this.sounds) {
      if (sound.getName().equals(name)) {
        return sound.isPlaying();
      }
    }

    return false;
  }

  /**
   * Sets the background color via a hue value
   *
   * @param h a hue value [0...255]
   */
  public void setColor(double h) {
    this.color.setHSB(h);
  }

  /**
   * Sets the background color via a rgb value
   *
   * @param r a red value [0...255]
   * @param g a green value [0...255]
   * @param b a blue value [0...255]
   */
  public void setColor(double r, double g, double b) {
    this.color.setRGB(r, g, b);
  }

  /**
   * Returns the current color of the stage.
   *
   * @return the current color
   */
  public Color getColor() {
    return this.color;
  }

  /**
   * Sets the color of the stage.
   *
   * @see org.openpatch.scratch.extensions.color.Color
   * @param c the new color to be set
   */
  public void setColor(Color c) {
    this.color = c;
  }

  /**
   * Changes the background color by adding a step to the hue value.
   *
   * @param h a step value
   */
  public void changeColor(double h) {
    this.color.changeColor(h);
  }

  /**
   * Sets the tint for the current backdrop with rgb.
   *
   * @see Image#setTint(double, double, double)
   * @param r a red value [0...255]
   * @param g a green value [0...255]
   * @param b a blue value [0...255]
   */
  public void setTint(double r, double g, double b) {
    if (this.backdrops.size() == 0)
      return;
    this.backdrops.get(this.currentBackdrop).setTint(r, g, b);
  }

  /**
   * Sets the tint for the current backdrop with a hue.
   *
   * @see Image#setTint(double)
   */
  public void setTint(double h) {
    if (this.backdrops.size() == 0)
      return;
    this.backdrops.get(this.currentBackdrop).setTint(h);
  }

  /**
   * Changes the tint for the current backdrop
   *
   * @see Image#changeTint(double)
   * @param step a step value
   */
  public void changeTint(double step) {
    if (this.backdrops.size() == 0)
      return;

    this.backdrops.get(this.currentBackdrop).changeTint(step);
  }

  /**
   * Sets the transparency of the current backdrop.
   *
   * @see Image#setTransparency(double)
   * @param transparency a transparency value [0...1]
   */
  public void setTransparency(double transparency) {
    this.backdrops.get(this.currentBackdrop).setTransparency(transparency);
  }

  /**
   * Changes the transparency for the current costume.
   *
   * @see Image#changeTransparency(double)
   * @param step a step value
   */
  public void changeTransparency(double step) {
    if (this.backdrops.size() == 0)
      return;

    this.backdrops.get(this.currentBackdrop).changeTransparency(step);
  }

  /**
   * Return the width of the current costume or the pen size, when no costume is
   * available.
   *
   * @return the width of the sprite
   */
  public int getWidth() {
    return Applet.getInstance().getRenderWidth();
  }

  /**
   * Return the height of the current costume or the pen size, when no costume is
   * available.
   *
   * @return the height of the sprite
   */
  public int getHeight() {
    return Applet.getInstance().getRenderHeight();
  }

  /**
   * Returns the timer
   *
   * @return the timer
   */
  public Timer getTimer() {
    return this.timer.get("default");
  }

  /**
   * Returns a timer by name
   *
   * @param name a name
   * @return the timer
   */
  public Timer getTimer(String name) {
    return this.timer.get(name);
  }

  /**
   * Add a new timer by name. Overwriting default is not permitted.
   *
   * @param name the name of the timer
   */
  public void addTimer(String name) {
    if ("default".equals(name))
      return;

    this.timer.put(name, new Timer());
  }

  /**
   * Remove a timer by name. Removing of default is not permitted.
   *
   * @param name the name of the timer
   */
  public void removeTimer(String name) {
    if ("default".equals(name))
      return;

    this.timer.remove(name);
  }

  public void mouseEvent(MouseEvent e) {
    this.mouseDown = false;

    if (e.getAction() == MouseEvent.PRESS) {
      this.mouseDown = true;
    } else if (e.getAction() == MouseEvent.CLICK) {
      final MouseCode me;
      if (e.getButton() == PConstants.LEFT) {
        me = MouseCode.LEFT;
      } else if (e.getButton() == PConstants.RIGHT) {
        me = MouseCode.RIGHT;
      } else if (e.getButton() == PConstants.CENTER) {
        me = MouseCode.CENTER;
      } else {
        me = null;
      }
      whenMouseClicked(me);
      this.sprites.stream()
          .forEach(
              s -> {
                s.mouseEvent(e);
                if (s.isTouchingMousePointer()) {
                  s.whenClicked();
                }
                if (me != null) {
                  s.whenMouseClicked(me);
                }
              });
    } else if (e.getAction() == MouseEvent.MOVE) {
      this.sprites.stream().forEach(s -> s.whenMouseMoved(this.mouseX, this.mouseY));
    } else if (e.getAction() == MouseEvent.WHEEL) {
      this.whenMouseWheelMoved(e.getCount());
    }
  }

  /**
   * This method is called when a mouse click event occurs. Overwrite this method
   * to add custom
   * behavior.
   *
   * @param mouseEvent The mouse event that triggered this method.
   */
  public void whenMouseClicked(MouseCode mouseEvent) {
  }

  /**
   * This method is called when the mouse wheel is moved. Overwrite this method to
   * add custom
   * behavior.
   *
   * @param steps the number of steps the mouse wheel has moved. Positive values
   *              indicate movement
   *              away from the user, while negative values indicate movement
   *              towards the user.
   */
  public void whenMouseWheelMoved(int steps) {
  }

  /**
   * Returns the current x-position of the mouse cursor
   *
   * @return x-position
   */
  public double getMouseX() {
    return this.mouseX;
  }

  /**
   * Returns the current y-position of the mouse cursor
   *
   * @return y-position
   */
  public double getMouseY() {
    return this.mouseY;
  }

  public Vector2 getMouse() {
    return new Vector2(this.mouseX, this.mouseY);
  }

  /**
   * Returns true is the mouse button is down
   *
   * @return mouse button down
   */
  public boolean isMouseDown() {
    return this.mouseDown;
  }

  /**
   * This method is called when a key is pressed. Override this method to add
   * custom behavior.
   *
   * @param keyCode the code of the key that was pressed
   */
  public void whenKeyPressed(int keyCode) {
  }

  /**
   * This method is called when a key is released. Override this method to add
   * custom behavior.
   *
   * @param keyCode the code of the key that was released
   */
  public void whenKeyReleased(int keyCode) {
  }

  public void keyEvent(KeyEvent e) {
    switch (e.getAction()) {
      case KeyEvent.PRESS:
        this.whenKeyPressed(e.getKeyCode());
        this.keyCodePressed.put(e.getKeyCode(), true);
        break;
      case KeyEvent.RELEASE:
        this.whenKeyReleased(e.getKeyCode());
        this.keyCodePressed.put(e.getKeyCode(), false);
        break;
    }
    this.sprites.stream().forEach(s -> s.keyEvent(e));
  }

  /**
   * Returns true if the key is pressed
   *
   * @param keyCode a key code
   * @return key pressed
   */
  public boolean isKeyPressed(int keyCode) {
    Boolean isPressed = this.keyCodePressed.get(keyCode);
    if (isPressed == null) {
      return false;
    }
    return isPressed;
  }

  /**
   * Gets the seconds passed since the last frame.
   *
   * @return secons since last frame
   */
  public double getDeltaTime() {
    return Window.getInstance().getDeltaTime();
  }

  /**
   * Returns the current year
   *
   * @return current year
   */
  public int getCurrentYear() {
    LocalDateTime now = LocalDateTime.now();
    return now.getYear();
  }

  /**
   * Returns the current month
   *
   * @return current month
   */
  public int getCurrentMonth() {
    LocalDateTime now = LocalDateTime.now();
    return now.getMonthValue();
  }

  /**
   * Returns the current day of the week
   *
   * @return current day of the week
   */
  public int getCurrentDayOfWeek() {
    LocalDateTime now = LocalDateTime.now();
    return now.getDayOfWeek().getValue();
  }

  /**
   * Returns the current day of the month
   *
   * @return current day of the month
   */
  public int getCurrentDay() {
    LocalDateTime now = LocalDateTime.now();
    return now.getDayOfMonth();
  }

  /**
   * Returns the current hour
   *
   * @return current hour
   */
  public int getCurrentHour() {
    LocalDateTime now = LocalDateTime.now();
    return now.getHour();
  }

  /**
   * Returns the current minute
   *
   * @return current minute
   */
  public int getCurrentMinute() {
    LocalDateTime now = LocalDateTime.now();
    return now.getMinute();
  }

  /**
   * Returns the current second
   *
   * @return current second
   */
  public int getCurrentSecond() {
    LocalDateTime now = LocalDateTime.now();
    return now.getSecond();
  }

  /**
   * Returns the current millisecond
   *
   * @return current millisecond
   */
  public int getCurrentMillisecond() {
    LocalDateTime now = LocalDateTime.now();
    return (int) Math.round(now.getNano() / 1000000.0);
  }

  /**
   * Returns the days since 2010/01/01
   *
   * @return days since 2010/01/01
   */
  public int getDaysSince2000() {
    LocalDate now = LocalDate.now();
    LocalDate then = LocalDate.of(2000, Month.JANUARY, 1);
    long c = ChronoUnit.DAYS.between(then, now);
    return (int) c;
  }

  /**
   * Returns a random integer between the specified range (inclusive).
   *
   * @param from the lower bound of the range (inclusive)
   * @param to   the upper bound of the range (inclusive)
   * @return a random integer between {@code from} and {@code to} (inclusive)
   */
  public int pickRandom(int from, final int to) {
    if (to < from) {
      return from + (int) (Math.random() * (to - from + 1));
    }
    return from + (int) (Math.random() * (to - from + 1));
  }

  /**
   * Displays the given text on the stage.
   *
   * @param text the text to be displayed
   */
  public void display(String text) {
    this.display.showText(text);
  }

  /**
   * Displays the given text on the screen for a specified duration.
   *
   * @param text   The text to be displayed.
   * @param millis The duration in milliseconds for which the text will be
   *               displayed.
   */
  public void display(String text, final int millis) {
    this.display.showText(text, millis);
  }

  /**
   * Broadcasts a message to all sprites in the stage. Each sprite will execute
   * its `whenIReceive`
   * method with the given message.
   *
   * @param message The message to broadcast to all sprites.
   */
  public void broadcast(String message) {
    this.sprites.stream().forEach(s -> s.whenIReceive(message));
  }

  /**
   * Broadcasts a message to all sprites in the stage. Each sprite will execute
   * its `whenIReceive`
   * method with the given message.
   *
   * @param message The message to broadcast to all sprites.
   */
  public void broadcast(Object message) {
    this.sprites.stream().forEach(s -> s.whenIReceive(message));
  }

  /**
   * This method is called when a specific message is received. Override this
   * method to add custom
   * behavior.
   *
   * @param message The message that triggers this method.
   */
  public void whenIReceive(String message) {
  }

  /**
   * This method is called when a message is received.
   *
   * @param message The message object that is received.
   */
  public void whenIReceive(Object message) {
  }

  /**
   * Sets the cursor image for the stage.
   *
   * @param path the file path to the cursor image
   */
  public void setCursor(String path) {
    this.cursor = path;
    this.cursorActiveSpotX = 0;
    this.cursorActiveSpotY = 0;
  }

  /**
   * Sets the cursor image and its active spot coordinates.
   *
   * @param path the file path to the cursor image
   * @param x    the x-coordinate of the cursor's active spot
   * @param y    the y-coordinate of the cursor's active spot
   */
  public void setCursor(String path, int x, int y) {
    this.cursor = path;
    this.cursorActiveSpotX = x;
    this.cursorActiveSpotY = y;
  }

  /**
   * Retrieves the current camera instance associated with this stage.
   *
   * @return the current Camera object.
   */
  public Camera getCamera() {
    return this.camera;
  }

  /**
   * Stop the execution of the whole applications for the given milliseconds.
   *
   * @param millis Milliseconds
   */
  public void wait(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }

  public double getFrameRate() {
    return Applet.getInstance().frameRate;
  }

  /**
   * Executes the main logic of the stage. This method should be overridden by
   * subclasses to define
   * the specific behavior of the stage.
   */
  public void run() {
  }

  public void pre() {
    Applet applet = Applet.getInstance();
    if (applet == null)
      return;
    var targetWidth = applet.getRenderWidth();
    var targetHeight = applet.getRenderHeight();
    var width = applet.getWidth();
    var height = applet.getHeight();

    var widthRatio = (float) width / targetWidth;
    var heightRatio = (float) height / targetHeight;

    var ratio = Math.min(widthRatio, heightRatio);
    width = (int) (targetWidth * ratio);
    height = (int) (targetHeight * ratio);

    var widthOffset = (applet.getWidth() - width) / 2;
    var heightOffset = (applet.getHeight() - height) / 2;

    var globalMouseX = (applet.mouseX - widthOffset) / (float) ratio - this.getWidth() / 2;
    var globalMouseY = -((applet.mouseY - heightOffset) / (float) ratio - this.getHeight() / 2);

    this.mouseX = this.getCamera().toLocalX(globalMouseX);
    this.mouseY = this.getCamera().toLocalY(globalMouseY);

    this.run();
    this.sprites.stream().forEach(s -> s.run());
  }

  /** Close the window and therefore the whole application. */
  public void exit() {
    Window.getInstance().exit();
  }

  public void draw() {
    Applet applet = Applet.getInstance();
    if (applet == null)
      return;

    applet.background(0);

    var targetWidth = applet.getRenderWidth();
    var targetHeight = applet.getRenderHeight();
    var width = applet.getWidth();
    var height = applet.getHeight();

    var widthRatio = (float) width / targetWidth;
    var heightRatio = (float) height / targetHeight;

    var ratio = Math.min(widthRatio, heightRatio);
    width = (int) (targetWidth * ratio);
    height = (int) (targetHeight * ratio);

    this.backdropBuffer.beginDraw();
    this.backdropBuffer.noStroke();
    this.backdropBuffer.background(
        (float) this.color.getRed(), (float) this.color.getGreen(), (float) this.color.getBlue());
    this.backdropBuffer.push();
    this.backdropBuffer.scale((float) this.camera.getZoom() / 100.0f);
    this.backdropBuffer.translate((float) -this.camera.getX(), (float) this.camera.getY());
    if (this.backdrops.size() > 0) {
      this.backdrops.get(this.currentBackdrop).drawAsBackground(this.backdropBuffer);
    }
    this.backdropBuffer.pop();
    this.backdropBuffer.endDraw();

    this.backgroundBuffer.beginDraw();
    this.backgroundBuffer.noStroke();
    this.backgroundBuffer.translate(this.getWidth() / 2.0f, this.getHeight() / 2.0f);
    this.backgroundBuffer.scale((float) this.camera.getZoom() / 100.0f);
    this.backgroundBuffer.translate((float) -this.camera.getX(), (float) this.camera.getY());
    if (this.eraseBackgroundBuffer) {
      this.backgroundBuffer.clear();
      this.eraseBackgroundBuffer = false;
    }
    this.pens.stream().filter(p -> p.isInBackground()).forEach(p -> p.draw(this.backgroundBuffer));
    this.sprites.stream()
        .filter(s -> s.getPen().isInBackground())
        .forEach(s -> s.getPen().draw(this.backgroundBuffer));
    while (!this.backgroundStamps.isEmpty()) {
      this.backgroundStamps.poll().draw(this.backgroundBuffer);
    }
    this.backgroundBuffer.endDraw();

    if (this.sorter != null) {
      this.sprites.sort(this.sorter);
    }

    if (this.cursor != null) {
      applet.cursor(Image.loadImage(this.cursor), this.cursorActiveSpotX, this.cursorActiveSpotY);
    }

    mainBuffer.beginDraw();
    mainBuffer.clear();
    mainBuffer.translate(this.getWidth() / 2.0f, this.getHeight() / 2.0f);
    mainBuffer.scale((float) this.camera.getZoom() / 100.0f);
    mainBuffer.translate((float) -this.camera.getX(), (float) this.camera.getY());

    this.sprites.stream().filter(s -> !s.isUI()).forEach(s -> s.draw(mainBuffer));
    this.texts.stream().filter(t -> !t.isUI()).forEach(t -> t.draw(mainBuffer));
    this.sprites.stream().filter(s -> !s.isUI()).forEach(s -> s.getText().draw(mainBuffer));
    mainBuffer.endDraw();

    // draw foreground
    this.foregroundBuffer.beginDraw();
    this.foregroundBuffer.translate(this.getWidth() / 2.0f, this.getHeight() / 2.0f);
    this.foregroundBuffer.scale((float) this.camera.getZoom() / 100.0f);
    this.foregroundBuffer.translate((float) -this.camera.getX(), (float) this.camera.getY());
    if (this.eraseForegroundBuffer) {
      this.foregroundBuffer.clear();
      this.eraseForegroundBuffer = false;
    }
    this.pens.stream().filter(p -> !p.isInBackground()).forEach(p -> p.draw(this.foregroundBuffer));
    this.sprites.stream()
        .filter(s -> !s.getPen().isInBackground())
        .forEach(s -> s.getPen().draw(this.foregroundBuffer));
    while (!this.foregroundStamps.isEmpty()) {
      this.foregroundStamps.poll().draw(this.foregroundBuffer);
    }
    this.foregroundBuffer.endDraw();

    shaderBuffer.beginDraw();
    var shader = this.getCurrentShader();
    if (shader != null) {
      shaderBuffer.shader(shader.getPShader());
    }
    shaderBuffer.clear();
    shaderBuffer.image(this.backdropBuffer, 0, 0);
    shaderBuffer.image(this.backgroundBuffer, 0, 0);
    shaderBuffer.image(this.mainBuffer, 0, 0);
    shaderBuffer.image(this.foregroundBuffer, 0, 0);
    shaderBuffer.resetShader();
    shaderBuffer.endDraw();
    applet.image(this.shaderBuffer, applet.width / 2.0f, applet.height / 2.0f, width, height);

    // draw ui
    this.uiBuffer.beginDraw();
    this.uiBuffer.translate(this.getWidth() / 2.0f, this.getHeight() / 2.0f);
    if (this.eraseUIBuffer) {
      this.uiBuffer.clear();
      this.eraseUIBuffer = false;
    }
    this.sprites.stream().filter(s -> s.isUI()).forEach(s -> s.getPen().draw(this.uiBuffer));
    while (!this.uiStamps.isEmpty()) {
      this.uiStamps.poll().draw(this.uiBuffer);
    }
    if (this.display != null) {
      this.display.draw(this.uiBuffer);
    }
    this.sprites.stream().filter(s -> s.isUI()).forEach(s -> s.draw(uiBuffer));
    this.texts.stream().filter(t -> t.isUI()).forEach(t -> t.draw(uiBuffer));
    this.sprites.stream().filter(s -> s.isUI()).forEach(s -> s.getText().draw(uiBuffer));
    this.uiBuffer.endDraw();

    if (this.uiBuffer.pixels != null) {
      applet.image(this.uiBuffer, applet.width / 2.0f, applet.height / 2.0f, width, height);
    } else {
      try {
        this.uiBuffer.loadPixels();
      } catch (Exception e) {
      }
    }

    if (applet.isDebug()) {
      this.debugBuffer.beginDraw();
      this.debugBuffer.translate(this.getWidth() / 2.0f, this.getHeight() / 2.0f);
      this.debugBuffer.pushMatrix();
      this.debugBuffer.scale((float) this.camera.getZoom() / 100.0f);
      this.debugBuffer.translate((float) -this.camera.getX(), (float) this.camera.getY());
      this.debugBuffer.clear();
      this.sprites.stream().filter(s -> !s.isUI()).forEach(s -> s.drawDebug(this.debugBuffer));
      this.debugBuffer.popMatrix();
      this.sprites.stream().filter(s -> s.isUI()).forEach(s -> s.drawDebug(this.debugBuffer));
      this.debugBuffer.strokeWeight(1);
      this.debugBuffer.stroke(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
      this.debugBuffer.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
      var globalMouseX = this.getCamera().toGlobalX(this.mouseX);
      var globalMouseY = this.getCamera().toGlobalY(this.mouseY);
      this.debugBuffer.line(
          (float) globalMouseX,
          -this.debugBuffer.height / 2,
          (float) globalMouseX,
          this.debugBuffer.height);
      this.debugBuffer.line(
          -this.debugBuffer.width / 2,
          (float) -globalMouseY,
          this.debugBuffer.width,
          (float) -globalMouseY);
      this.debugBuffer.textFont(Font.getDefaultFont());
      this.debugBuffer.text(
          "("
              + Math.round(this.mouseX * 100) / 100.0
              + ", "
              + Math.round(this.mouseY * 100) / 100.0
              + ")",
          (float) globalMouseX,
          (float) -globalMouseY);
      this.debugBuffer.pushMatrix();
      this.debugBuffer.translate(-this.debugBuffer.width / 2.0f, -this.debugBuffer.height / 2.0f);
      this.debugBuffer.text("FPS: " + Math.round(applet.frameRate * 100) / 100, 20, 20);
      this.debugBuffer.text(
          "Camera: ("
              + Math.round(this.camera.getX() * 100) / 100.0
              + ", "
              + Math.round(this.camera.getY() * 100) / 100.0
              + ") "
              + Math.round(this.camera.getZoom() * 100) / 100,
          20,
          40);
      this.debugBuffer.popMatrix();
      this.debugBuffer.endDraw();

      applet.image(this.debugBuffer, applet.width / 2.0f, applet.height / 2.0f, width, height);
    }
  }
}
