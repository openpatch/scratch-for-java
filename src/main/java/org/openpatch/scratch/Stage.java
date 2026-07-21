package org.openpatch.scratch;

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
import org.openpatch.scratch.extensions.pixels.Pixels;
import org.openpatch.scratch.extensions.shader.Shader;
import org.openpatch.scratch.extensions.shader.Shaders;
import org.openpatch.scratch.extensions.sorting.Sorting;
import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.Font;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Sound;
import org.openpatch.scratch.internal.Stamp;
import org.openpatch.scratch.internal.StageHooks;
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
 * 
 * @index-in-docs 2
 */
public class Stage {
  /**
   * Lets the render loop reach this stage without the four methods above having
   * to be public. See {@link StageHooks}.
   */
  private final StageHooks hooks = new StageHooks() {
    public void pre() {
      Stage.this.pre();
    }

    public void draw(PGraphics buffer) {
      Stage.this.draw(buffer);
    }

    public void keyEvent(KeyEvent e) {
      Stage.this.keyEvent(e);
    }

    public void mouseEvent(MouseEvent e) {
      Stage.this.mouseEvent(e);
    }
  };

  private final Shaders shaders = new Shaders("stage");

  private final List<Image> backdrops = new CopyOnWriteArrayList<>();
  private Color color = new Color();
  private int currentBackdrop = 0;
  private final List<Sound> sounds = new CopyOnWriteArrayList<>();

  private PGraphics shaderBuffer;
  private PGraphics mainBuffer;

  private PGraphics backdropBuffer;

  private Queue<Stamp> backgroundStamps;
  private PGraphics backgroundBuffer;
  private boolean eraseBackgroundBuffer;

  private Queue<Stamp> foregroundStamps;
  private PGraphics foregroundBuffer;
  private boolean eraseForegroundBuffer;

  private Queue<Stamp> uiStamps;
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
  private final Sorting sorting = new Sorting();
  private Pixels pixels;

  Hitbox leftBorder;
  Hitbox rightBorder;
  Hitbox topBorder;
  Hitbox bottomBorder;

  private Camera camera;
  private final java.util.Set<String> warnedOnce = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

  private void warnOnce(String key, String... lines) {
    if (warnedOnce.add(key)) {
      System.err.println("\n==============================================");
      for (String line : lines)
        System.err.println(line);
      System.err.println("==============================================\n");
    }
  }

  /**
   * Returns the order in which the sprites of this stage are drawn.
   *
   * <p>
   * Example usage:
   *
   * <pre>{@code
   * this.getSorting().byY();
   * }</pre>
   *
   * @return the sorting
   */
  public Sorting getSorting() {
    return this.sorting;
  }

  /**
   * Returns the colours of everything this stage has drawn.
   *
   * <p>
   * Example usage:
   *
   * <pre>{@code
   * int[] colours = this.getPixels().main();
   * }</pre>
   *
   * @return the pixels
   */
  public Pixels getPixels() {
    return this.pixels;
  }

  /**
   * Returns the shaders of this stage. Shader handling lives behind this one
   * method so that it does not crowd the everyday API.
   *
   * <p>
   * Example usage:
   *
   * <pre>{@code
   * this.getShaders().add("blur", "blur.frag", null);
   * this.getShaders().switchTo("blur");
   * }</pre>
   *
   * @return the shaders
   */
  public Shaders getShaders() {
    return this.shaders;
  }

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
    applet.registerHooks(this, this.hooks);

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
    this.pixels = new Pixels(this.mainBuffer, this.backgroundBuffer, this.foregroundBuffer);
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
   * Prints a debug message to stdout when debug mode is enabled.
   * The message is prefixed with the stage's class name so you can tell
   * which stage it came from.
   *
   * <p>Example:
   * <pre>{@code
   * this.debug("score =", score, "lives =", lives);
   * // prints: [MyStage] score = 5 lives = 3
   * }</pre>
   *
   * @param values one or more values to print
   */
  public void debug(Object... values) {
    if (!Applet.getInstance().isDebug()) return;
    StringBuilder sb = new StringBuilder("[").append(getClass().getSimpleName()).append("]");
    for (Object v : values) {
      sb.append(" ").append(v);
    }
    System.out.println(sb);
  }

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

  // Package-private: the implementation behind Sprite.goToFrontLayer().
  void goToFrontLayer(Sprite sprite) {
    this.sprites.remove(sprite);
    this.sprites.add(sprite);
  }

  // Package-private: the implementation behind Sprite.goToBackLayer().
  void goToBackLayer(Sprite sprite) {
    this.sprites.remove(sprite);
    this.sprites.add(0, sprite);
  }

  // Package-private: the implementation behind Sprite.goLayersForwards().
  void goLayersForwards(Sprite sprite, int number) {
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

  // Package-private: the implementation behind Sprite.goLayersBackwards().
  void goLayersBackwards(Sprite sprite, int number) {
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

  // Private helper for removeAll().
  private void removeAllSprites() {
    for (Sprite sprite : this.sprites) {
      sprite.removedFromStage(this);
    }
    this.sprites.clear();
  }

  // Private helper for removeAll().
  private void removeAllTexts() {
    for (Text text : this.texts) {
      text.removedFromStage(this);
    }
    this.texts.clear();
  }

  // Private helper for removeAll().
  private void removeAllPens() {
    for (Pen pen : this.pens) {
      pen.removedFromStage(this);
    }
    this.pens.clear();
  }

  /** Removes all elements from the stage. */
  public void removeAll() {
    this.removeAllSprites();
    this.removeAllTexts();
    this.removeAllPens();
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
   * Returns the number of sprites of the specified class.
   *
   * @param c the class of the sprites to count
   * @return the number of sprites of the specified class
   */
  public <T extends Sprite> long count(Class<T> c) {
    return this.sprites.stream().filter(c::isInstance).count();
  }

  /**
   * Add a backdrop to the stage. If a backdrop with the received name already
   * exists do nothing.
   *
   * @param name      a unique name
   * @param imagePath a image path, or the name of a built-in sprite such as
   *                  "bg_castle"
   * @param stretch   stretch image to window size
   */
  public void addBackdrop(String name, final String imagePath, boolean stretch) {
    for (Image backdrop : this.backdrops) {
      if (backdrop.getName().equals(name)) {
        return;
      }
    }
    Image backdrop = Image.ofNameOrPath(name, imagePath);
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
   * @param imagePath a image path, or the name of a built-in sprite such as
   *                  "bg_castle"
   */
  public void addBackdrop(String name, final String imagePath) {
    this.addBackdrop(name, imagePath, false);
  }

  /**
   * Add one of the backdrops that ship with Scratch for Java to the stage. The
   * backdrop gets the same name as the built-in sprite. If a backdrop with that
   * name already exists do nothing.
   *
   * <p>
   * Example usage:
   *
   * <pre>{@code
   * this.addBackdrop("bg_castle");
   * }</pre>
   *
   * @param name the name of a built-in sprite, for example "bg_castle". Add the
   *             sheet in front of the name, for example "platformer/grass", if
   *             the same name exists on several sheets.
   */
  public void addBackdrop(String name) {
    this.addBackdrop(name, name, false);
  }

  /**
   * Switch to a backdrop by name.
   *
   * @scratchblock switch backdrop to [name v]
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
    System.err.println("\n==============================================");
    System.err.println("WARNING: Backdrop not found!");
    System.err.println("==============================================");
    System.err.println("Backdrop name: '" + name + "'");
    if (this.backdrops.isEmpty()) {
      System.err.println("\nThis stage has no backdrops.");
      System.err.println("\nTip: Use addBackdrop() to add backdrops first.");
    } else {
      System.err.println("\nAvailable backdrops:");
      for (Image backdrop : this.backdrops) {
        System.err.println("  - '" + backdrop.getName() + "'");
      }
      System.err.println("\nTip: Check the spelling of your backdrop name.");
    }
    System.err.println("==============================================\n");
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

  /**
   * Switch to the next backdrop.
   *
   * @scratchblock next backdrop
   */
  public void nextBackdrop() {
    if (this.backdrops.isEmpty()) {
      System.err.println("\n==============================================");
      System.err.println("WARNING: No backdrops added!");
      System.err.println("==============================================");
      System.err.println("\nCannot switch to next backdrop - stage has no backdrops.");
      System.err.println("\nTip: Use addBackdrop() to add at least one backdrop");
      System.err.println("     before calling nextBackdrop().");
      System.err.println("==============================================\n");
      return;
    }
    this.currentBackdrop = (this.currentBackdrop + 1) % this.backdrops.size();
    this.emitBackdropSwitch();
  }

  /** Switch to the previous backdrop. */
  public void previousBackdrop() {
    if (this.backdrops.isEmpty()) {
      System.err.println("\n==============================================");
      System.err.println("WARNING: No backdrops added!");
      System.err.println("==============================================");
      System.err.println("\nCannot switch to previous backdrop - stage has no backdrops.");
      System.err.println("\nTip: Use addBackdrop() to add at least one backdrop");
      System.err.println("     before calling previousBackdrop().");
      System.err.println("==============================================\n");
      return;
    }
    var index = (this.currentBackdrop - 1) % this.backdrops.size();
    if (index < 0) {
      index += this.backdrops.size();
    }
    this.currentBackdrop = index;
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

  /** When this method is called, the background buffer will be erased. *
   */
  void eraseBackground() {
    this.eraseBackgroundBuffer = true;
  }

  /** When this method is called, the foreground buffer will be erased. *
   */
  void eraseForeground() {
    this.eraseForegroundBuffer = true;
  }




  /**
   * This method marks the UI buffer to be erased, which will be processed in the
   * next update cycle.
   *
   */
  void eraseUI() {
    this.eraseUIBuffer = true;
  }

  /**
   * Add a sound to the stage. If a sound with the received name already exists do
   * nothing.
   *
   * @param name      a unique name
   * @param soundPath a sound path, or the name of a built-in sound such as
   *                  "footstep_carpet_000"
   */
  public void addSound(String name, final String soundPath) {
    for (Sound sound : this.sounds) {
      if (sound.getName().equals(name)) {
        return;
      }
    }

    Sound sound = Sound.ofNameOrPath(name, soundPath);
    this.sounds.add(sound);
  }

  /**
   * Add one of the sounds that ship with Scratch for Java to the stage. The
   * sound gets the same name as the built-in sound. If a sound with that name
   * already exists do nothing.
   *
   * <p>
   * Example usage:
   *
   * <pre>{@code
   * this.addSound("footstep_carpet_000");
   * }</pre>
   *
   * @param name the name of a built-in sound, for example "footstep_carpet_000"
   */
  public void addSound(String name) {
    this.addSound(name, name);
  }

  /**
   * Plays a sound.
   *
   * @param name the sound name
   */
  public void playSound(String name) {
    boolean found = false;
    for (Sound sound : this.sounds) {
      if (sound.getName().equals(name)) {
        found = true;
        if (!sound.isPlaying()) {
          sound.play();
        }
      }
    }
    if (!found) {
      this.printSoundNotFoundWarning(name);
    }
  }

  private void printSoundNotFoundWarning(String name) {
    System.err.println("\n==============================================");
    System.err.println("WARNING: Sound not found!");
    System.err.println("==============================================");
    System.err.println("Sound name: '" + name + "'");
    if (this.sounds.isEmpty()) {
      System.err.println("\nAvailable sounds: none added");
    } else {
      System.err.println("\nAvailable sounds:");
      for (Sound sound : this.sounds) {
        System.err.println("  - '" + sound.getName() + "'");
      }
    }
    System.err.println("\nTip: Check the spelling of your sound name.");
    System.err.println("==============================================\n");
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
    boolean found = false;
    for (Sound sound : this.sounds) {
      if (sound.getName().equals(name)) {
        found = true;
        sound.stop();
        break;
      }
    }
    if (!found) {
      this.printSoundNotFoundWarning(name);
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
    // Created on first use, so that a timer never has to be declared up front.
    return this.timer.computeIfAbsent(name, n -> new Timer());
  }



  /**
   * @ignore-in-docs
   * @param e
   */
  private void mouseEvent(MouseEvent e) {
    if (e.getAction() == MouseEvent.CLICK) {
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

  /**
   * Returns the current position of the mouse cursor as a Vector2
   *
   * @return mouse position
   */
  public Vector2 getMouse() {
    return new Vector2(this.mouseX, this.mouseY);
  }

  /**
   * Returns true is the mouse button is down
   *
   * @return mouse button down
   */
  public boolean isMouseDown() {
    return Applet.getInstance().isMouseDown();
  }

  /**
   * This method is called when a key is pressed. Override this method to add
   * custom behavior.
   *
   * @param keyCode the key that was pressed
   */
  public void whenKeyPressed(KeyCode keyCode) {
  }

  /**
   * This method is called when a key is released. Override this method to add
   * custom behavior.
   *
   * @param keyCode the key that was released
   */
  public void whenKeyReleased(KeyCode keyCode) {
  }

  /**
   * @ignore-in-docs
   * @param e
   */
  private void keyEvent(KeyEvent e) {
    switch (e.getAction()) {
      case KeyEvent.PRESS:
        this.whenKeyPressed(KeyCode.fromCode(e.getKeyCode()));
        break;
      case KeyEvent.RELEASE:
        this.whenKeyReleased(KeyCode.fromCode(e.getKeyCode()));
        break;
    }
    this.sprites.stream().forEach(s -> s.keyEvent(e));
  }

  /**
   * Returns true if the key is pressed
   *
   * @param keyCode a key
   * @return key pressed
   */
  public boolean isKeyPressed(KeyCode keyCode) {
    var kp = Applet.getInstance().getKeyCodePressed();
    Boolean isPressed = kp.get(keyCode.getCode());
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
   * This method is called when a specific message is received. Override this
   * method to add custom
   * behavior.
   *
   * @param message The message that triggers this method.
   */
  public void whenIReceive(String message) {
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

  /**
   * Get the frame rate of the application.
   *
   * @return the frame rate
   */
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

  /**
   * Stamps images permanently onto one of the stage's layers. A stamp stays
   * where it is put until that layer is erased, so this is how a tile map or a
   * painted background gets drawn once instead of every frame.
   *
   * <p>
   * Example usage:
   *
   * <pre>{@code
   * stage.stamp(stamps, Layer.BACKGROUND);
   * }</pre>
   *
   * @param stamps the images to stamp
   * @param layer  which layer to stamp them onto
   */
  public void stamp(Queue<Stamp> stamps, Layer layer) {
    if (stamps == null || layer == null) {
      return;
    }
    switch (layer) {
      case BACKGROUND -> this.addStampsToBackground(stamps);
      case FOREGROUND -> this.addStampsToForeground(stamps);
      case UI -> this.addStampsToUI(stamps);
    }
  }

  /**
   * @ignore-in-docs
   * @param stamp
   */
  void addStampsToForeground(Stamp stamp) {
    if (stamp == null) {
      return;
    }
    this.foregroundStamps.add(stamp);
  }

  /**
   * @ignore-in-docs
   * @param stamps
   */
  void addStampsToForeground(Queue<Stamp> stamps) {
    this.foregroundStamps.addAll(stamps);
  }

  /**
   * @ignore-in-docs
   * @param stamp
   */
  void addStampsToBackground(Stamp stamp) {
    if (stamp == null) {
      return;
    }
    this.backgroundStamps.add(stamp);
  }

  /**
   * @ignore-in-docs
   * @param stamps
   */
  void addStampsToBackground(Queue<Stamp> stamps) {
    this.backgroundStamps.addAll(stamps);
  }

  /**
   * @ignore-in-docs
   * @param stamp
   */
  void addStampsToUI(Stamp stamp) {
    if (stamp == null) {
      return;
    }
    this.uiStamps.add(stamp);
  }

  /**
   * @ignore-in-docs
   * @param stamps
   */
  void addStampsToUI(Queue<Stamp> stamps) {
    this.uiStamps.addAll(stamps);
  }

  /**
   * @ignore-in-docs
   */
  private void pre() {
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

  /**
   * @ignore-in-docs
   * @param buffer
   */
  private void draw(PGraphics buffer) {
    Applet applet = Applet.getInstance();
    if (applet == null || buffer == null)
      return;

    buffer.background(0);

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

    if (this.sorting.isOn()) {
      this.sprites.sort(this.sorting.getComparator());
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
    var shader = this.shaders.getCurrent();
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
    buffer.image(this.shaderBuffer, buffer.width / 2.0f, buffer.height / 2.0f, width, height);

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
      buffer.image(this.uiBuffer, applet.width / 2.0f, applet.height / 2.0f, width, height);
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

      buffer.image(this.debugBuffer, applet.width / 2.0f, applet.height / 2.0f, width, height);
    }
  }
}
