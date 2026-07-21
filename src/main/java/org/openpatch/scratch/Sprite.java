package org.openpatch.scratch;

import java.util.AbstractMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.openpatch.scratch.internal.Utils;
import org.openpatch.scratch.extensions.shader.Shader;
import org.openpatch.scratch.extensions.shader.Shaders;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Sound;
import org.openpatch.scratch.internal.Stamp;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * The `Sprite` class represents a graphical object that can be displayed on a
 * stage. It supports
 * various functionalities such as costumes, sounds, movement, rotation,
 * collision detection, and
 * interaction with the mouse and keyboard.
 *
 * <p>
 * Key features include:
 *
 * <ul>
 * <li>Adding and switching costumes
 * <li>Adding and playing sounds
 * <li>Movement and rotation
 * <li>Collision detection with other sprites and the stage edges
 * <li>Interaction with the mouse and keyboard
 * <li>Displaying text and thought bubbles
 * <li>Broadcasting and receiving messages
 * </ul>
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * {@code
 * Sprite sprite = new Sprite();
 * sprite.addCostume("costume1", "path/to/image.png");
 * sprite.addSound("sound1", "path/to/sound.wav");
 * sprite.setPosition(100, 200);
 * sprite.move(10);
 * sprite.turnRight(90);
 * sprite.say("Hello, world!");
 * }
 * </pre>
 *
 * <p>
 * Note: This class is designed to be used within a stage, and many methods
 * require the sprite to
 * be added to a stage to function correctly.
 *
 * @see Stage
 * @see Image
 * @see Sound
 * @see Pen
 * @see Text
 * @see Timer
 * @see Hitbox
 * @index-in-docs 1
 */
public class Sprite {
  private Shaders shaders = new Shaders("sprite");

  /**
   * Returns the shaders of this sprite. Shader handling lives behind this one
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

  /** Direction constant pointing up (0 degrees).
   * @see #setDirection(double) */
  public static final double DIRECTION_UP = 0;

  /** Direction constant pointing right (90 degrees).
   * @see #setDirection(double) */
  public static final double DIRECTION_RIGHT = 90;

  /** Direction constant pointing down (180 degrees).
   * @see #setDirection(double) */
  public static final double DIRECTION_DOWN = 180;

  /** Direction constant pointing left (270 degrees).
   * @see #setDirection(double) */
  public static final double DIRECTION_LEFT = 270;

  private List<Image> costumes = new CopyOnWriteArrayList<>();
  private int currentCostume = 0;
  private List<Sound> sounds = new CopyOnWriteArrayList<>();
  private double volume = 100;
  private double glideFromX;
  private double glideFromY;
  private double glideToX;
  private double glideToY;
  private double glideMillis;
  private double glideElapsed = -1;
  private boolean show = true;
  private double size = 100;
  private boolean onEdgeBounce = false;
  private RotationStyle rotationStyle = RotationStyle.ALL_AROUND;
  private double x = 0;
  private double y = 0;
  private double direction = 90;
  private Stage stage;
  private final AbstractMap<String, Timer> timer;
  private final Pen pen;
  private Hitbox hitbox;
  private boolean hitboxDisabled = false;
  private final Text text;
  private boolean isUI;

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
   * Constructs a new Sprite object with default settings.
   */
  public Sprite() {
    this.pen = new Pen(this);
    this.timer = new ConcurrentHashMap<>();
    this.text = new Text(this);
    this.x = 0;
    this.y = 0;
    this.timer.put("default", new Timer());
  }

  /**
   * Constructs a new Sprite object with a specified costume.
   *
   * @param name      a unique name for the costume
   * @param imagePath the path to the image file for the costume
   */
  public Sprite(String name, final String imagePath) {
    this();
    Image costume = new Image(name, imagePath);
    this.costumes.add(costume);
  }

  /**
   * Copies a Sprite object.
   *
   * @param s a Sprite object to copy
   */
  public Sprite(Sprite s) {
    this.costumes = new CopyOnWriteArrayList<>();
    for (Image costume : s.costumes) {
      this.costumes.add(new Image(costume));
    }
    this.currentCostume = s.currentCostume;
    this.sounds = new CopyOnWriteArrayList<>();
    for (Sound sound : s.sounds) {
      this.sounds.add(new Sound(sound));
    }
    this.show = s.show;
    this.size = s.size;
    this.onEdgeBounce = s.onEdgeBounce;
    this.rotationStyle = s.rotationStyle;
    this.x = s.x;
    this.y = s.y;
    this.direction = s.direction;
    this.stage = s.stage;
    this.timer = new ConcurrentHashMap<>();
    this.timer.put("default", new Timer());
    this.pen = new Pen(s.pen);
    this.shaders = new Shaders(s.shaders);
    this.hitbox = s.hitbox;
    this.hitboxDisabled = s.hitboxDisabled;
    this.text = new Text(s.text);
    this.isUI = s.isUI;
  }

  /**
   * This method is called when the sprite is added to the stage. Override this
   * method to define
   * custom behavior when the sprite is added to the stage.
   */
  public void whenAddedToStage() {
  }


  /**
   * This method is called when the sprite is removed from the stage. Override
   * this method to define
   * custom behavior when the sprite is removed.
   */
  public void whenRemovedFromStage() {
  }


  /**
   * Removes this sprite from its current stage.
   *
   * @scratchblock delete this clone
   */
  public void remove() {
    if (this.stage != null) {
      this.stage.remove(this);
    }
  }

  /**
   * Prints a debug message to stdout when debug mode is enabled.
   * The message is prefixed with the sprite's class name so you can tell
   * which sprite it came from.
   *
   * <p>Example:
   * <pre>{@code
   * this.debug("x =", getX(), "y =", getY());
   * // prints: [CatSprite] x = 100.0 y = 200.0
   * }</pre>
   *
   * @param values one or more values to print
   */
  public void debug(Object... values) {
    if (this.stage == null || !this.stage.isDebug()) return;
    StringBuilder sb = new StringBuilder("[").append(getClass().getSimpleName()).append("]");
    for (Object v : values) {
      sb.append(" ").append(v);
    }
    System.out.println(sb);
  }

  /**
   * Retrieves the current stage associated with this sprite.
   *
   * @return the stage associated with this sprite
   */
  public Stage getStage() {
    return this.stage;
  }










  /**
   * Add one of the costumes that ship with Scratch for Java to the sprite. The
   * costume gets the same name as the built-in sprite. If a costume with that
   * name already exists do nothing.
   *
   * <p>
   * Example usage:
   *
   * <pre>{@code
   * this.addCostume("bunny1_jump");
   * }</pre>
   *
   * @param name the name of a built-in sprite, for example "bunny1_jump". Add
   *             the sheet in front of the name, for example
   *             "platformer/grass", if the same name exists on several sheets.
   *
   * @example.preview SpriteAddCostume.gif
   * @example.files SpriteAddCostume.java
   */
  public void addCostume(String name) {
    this.addCostume(name, name);
  }

  /**
   * Add a costume to the sprite. If a costume with the received name already
   * exists do nothing.
   *
   * @param name      a unique name
   * @param imagePath a image path, or the name of a built-in sprite such as
   *                  "bunny1_jump"
   */
  public void addCostume(String name, final String imagePath) {
    for (Image costume : this.costumes) {
      if (costume.getName().equals(name)) {
        return;
      }
    }

    Image costume = Image.ofNameOrPath(name, imagePath);
    this.costumes.add(costume);
  }

  /**
   * Adds a new costume to the sprite if a costume with the same name does not
   * already exist.
   *
   * @param name            The name of the costume.
   * @param spriteSheetPath The path to the sprite sheet image file.
   * @param x               The x-coordinate of the top-left corner of the costume
   *                        in the sprite sheet.
   * @param y               The y-coordinate of the top-left corner of the costume
   *                        in the sprite sheet.
   * @param width           The width of the costume in the sprite sheet.
   * @param height          The height of the costume in the sprite sheet.
   */
  public void addCostume(
      String name,
      final String spriteSheetPath,
      final int x,
      final int y,
      final int width,
      final int height) {
    for (Image costume : this.costumes) {
      if (costume.getName().equals(name)) {
        return;
      }
    }

    Image costume = new Image(name, spriteSheetPath, x, y, width, height);
    this.costumes.add(costume);
  }

  /**
   * Adds all tiles from a spritesheet as costumes. The costumes will be name by
   * the prefix and the
   * index in the spritesheet. For example if the prefix is "tile" and the
   * spritesheet contains 4
   * tiles, the costumes will be named "tile0", "tile1", "tile2", and "tile3".
   *
   * @param prefix      a prefix for all generated costumes
   * @param spriteSheet a path to a sprite sheet
   * @param tileWidth   the width of a single tile
   * @param tileHeight  the height of a single tile
   */
  public void addCostumes(String prefix, String spriteSheet, int tileWidth, int tileHeight) {
    var image = Image.loadImage(spriteSheet);

    var nx = image.width / tileWidth;
    var ny = image.height / tileHeight;

    for (var y = 0; y < ny; y += 1) {
      for (var x = 0; x < nx; x += 1) {
        var index = x * nx + y;
        Image costume = new Image(
            prefix + index, spriteSheet, x * tileWidth, y * tileHeight, tileWidth, tileHeight);
        this.costumes.add(costume);
      }
    }
  }

  /**
   * Set the nine-slice (also known as nine-patch) parameters for the sprite's
   * costumes.
   *
   * @param top    the size of the top slice in pixels
   * @param right  the size of the right slice in pixels
   * @param bottom the size of the bottom slice in pixels
   * @param left   the size of the left slice in pixels
   */
  protected void setNineSlice(int top, int right, int bottom, int left) {
    if (this.costumes.size() == 0)
      return;
    for (Image costume : this.costumes) {
      costume.setNineSlice(top, right, bottom, left);
    }
  }

  /** Disables the nine-slice feature for all costumes of the sprite. */
  protected void disableNineSlice() {
    if (this.costumes.size() == 0)
      return;
    for (Image costume : this.costumes) {
      costume.disableNineSlice();
    }
  }

  /**
   * Switch to a costume by name.
   *
   * @scratchblock switch costume to [name v]
   *
   * @param name the name of a costume
   *
   * @example.preview SpriteSwitchCostume.gif
   * @example.files SpriteSwitchCostume.java
   */
  public void switchCostume(String name) {
    for (int i = 0; i < this.costumes.size(); i++) {
      Image costume = this.costumes.get(i);
      if (costume.getName().equals(name)) {
        this.currentCostume = i;
        return;
      }
    }
    System.err.println("\n==============================================");
    System.err.println("WARNING: Costume not found!");
    System.err.println("==============================================");
    System.err.println("Costume name: '" + name + "'");
    if (this.costumes.isEmpty()) {
      System.err.println("\nThis sprite has no costumes.");
      System.err.println("\nTip: Use addCostume() to add costumes first.");
    } else {
      System.err.println("\nAvailable costumes:");
      for (Image costume : this.costumes) {
        System.err.println("  - '" + costume.getName() + "'");
      }
      System.err.println("\nTip: Check the spelling of your costume name.");
    }
    System.err.println("==============================================\n");
  }

  /**
   * Switches the current costume of the sprite to the costume at the specified
   * index.
   *
   * @param index The index of the costume to switch to.
   *
   * @scratchblock switch costume to (index)
   */
  public void switchCostume(double index) {
    this.currentCostume = (int) index % this.costumes.size();
  }

  /**
   * Switch to the next costume.
   *
   * @scratchblock next costume
   *
   * @example.preview SpriteNextCostume.gif
   * @example.files SpriteNextCostume.java
   */
  public void nextCostume() {
    if (this.costumes.isEmpty()) {
      System.err.println("\n==============================================");
      System.err.println("WARNING: No costumes added!");
      System.err.println("==============================================");
      System.err.println("\nCannot switch to next costume - sprite has no costumes.");
      System.err.println("\nTip: Use addCostume() to add at least one costume");
      System.err.println("     before calling nextCostume().");
      System.err.println("==============================================\n");
      return;
    }
    this.currentCostume = (this.currentCostume + 1) % this.costumes.size();
  }

  /** Switch to the next costume. */
  public void previousCostume() {
    if (this.costumes.isEmpty()) {
      System.err.println("\n==============================================");
      System.err.println("WARNING: No costumes added!");
      System.err.println("==============================================");
      System.err.println("\nCannot switch to previous costume - sprite has no costumes.");
      System.err.println("\nTip: Use addCostume() to add at least one costume");
      System.err.println("     before calling previousCostume().");
      System.err.println("==============================================\n");
      return;
    }
    var index = (this.currentCostume - 1) % this.costumes.size();
    if (index < 0) {
      index += this.costumes.size();
    }
    this.currentCostume = index;
  }

  /**
   * Returns the current costume name
   *
   * @return a costume name
   *
   * @example.preview SpriteGetCurrentCostumeName.gif
   * @example.files SpriteGetCurrentCostumeName.java
   *
   * @scratchblock (costume [name v])
   */
  public String getCurrentCostumeName() {
    if (this.costumes.size() == 0)
      return null;

    return this.costumes.get(this.currentCostume).getName();
  }

  /**
   * Returns the current costume index
   *
   * @return a costume index
   *
   * @example.preview SpriteGetCurrentCostumeIndex.gif
   * @example.files SpriteGetCurrentCostumeIndex.java
   *
   * @scratchblock (costume [number v])
   */
  public int getCurrentCostumeIndex() {
    return this.currentCostume;
  }

  /**
   * Add a sound to the sprite. If a sound with the received name already exists
   * do nothing.
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
   * Add one of the sounds that ship with Scratch for Java to the sprite. The
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
   * @scratchblock start sound [name v]
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
        break;
      }
    }
    if (!found) {
      System.err.println("\n==============================================");
      System.err.println("WARNING: Sound not found!");
      System.err.println("==============================================");
      System.err.println("Sound name: '" + name + "'");
      if (this.sounds.isEmpty()) {
        System.err.println("\nThis sprite has no sounds.");
        System.err.println("\nTip: Use addSound() to add sounds first.");
      } else {
        System.err.println("\nAvailable sounds:");
        for (Sound sound : this.sounds) {
          System.err.println("  - '" + sound.getName() + "'");
        }
        System.err.println("\nTip: Check the spelling of your sound name.");
      }
      System.err.println("==============================================\n");
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

  /**
   * Stops the playing of all sounds of the sprite.
   *
   * @scratchblock stop all sounds
   */
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
   * @param name Name of the sound
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
   * Sets the tint for the sprite with an color object.
   *
   * @see Color
   * @param c a color object
   *
   * @example.preview SpriteSetTint.gif
   * @example.files SpriteSetTint.java
   */
  public void setTint(Color c) {
    this.setTint(c.getRed(), c.getGreen(), c.getBlue());
  }

  /**
   * Sets the tint for the sprite with rgb.
   *
   * @see Image#setTint(double, double, double)
   * @param r a red value [0...255]
   * @param g a green value [0...255]
   * @param b a blue value [0...255]
   */
  public void setTint(double r, double g, double b) {
    if (this.costumes.size() == 0) {
      warnOnce("setTint-no-costume",
          "WARNING: setTint() called but this sprite has no costumes!",
          "",
          "Tip: Call addCostume() before using setTint().");
      return;
    }

    for (Image costume : this.costumes) {
      costume.setTint(r, g, b);
    }
  }

  /**
   * Sets the tint for the sprite with a hue.
   *
   * @see Image#setTint(double)
   * @param h a hue value [0...255]
   *
   * @scratchblock set [color v] effect to (h)
   */
  public void setTint(double h) {
    if (this.costumes.size() == 0) {
      warnOnce("setTint-h-no-costume",
          "WARNING: setTint() called but this sprite has no costumes!",
          "",
          "Tip: Call addCostume() before using setTint().");
      return;
    }

    for (Image costume : this.costumes) {
      costume.setTint(h);
    }
  }

  /**
   * Changes the tint for the sprite by a step.
   *
   * @param step a step value
   *
   * @example.preview SpriteChangeTint.gif
   * @example.files SpriteChangeTint.java
   *
   * @scratchblock change [color v] effect by (step)
   */
  public void changeTint(double step) {
    if (this.costumes.size() == 0) {
      warnOnce("changeTint-no-costume",
          "WARNING: changeTint() called but this sprite has no costumes!",
          "",
          "Tip: Call addCostume() before using changeTint().");
      return;
    }

    for (Image costume : this.costumes) {
      costume.changeTint(step);
    }
  }

  /**
   * Retrieves the tint value of the current costume.
   *
   * @return the tint value of the current costume, or null if there are no
   *         costumes.
   */
  public Color getTint() {
    if (this.costumes.size() == 0)
      return null;
    return this.costumes.get(currentCostume).getTint();
  }

  /**
   * Sets the transparency of the sprite.
   *
   * @see Image#setTransparency(double)
   * @param transparency 0 full transparency, 255 no transparency
   *
   * @example.preview SpriteSetTransparency.gif
   * @example.files SpriteSetTransparency.java
   *
   * @scratchblock set [ghost v] effect to (transparency)
   */
  public void setTransparency(double transparency) {
    if (this.costumes.size() == 0) {
      warnOnce("setTransparency-no-costume",
          "WARNING: setTransparency() called but this sprite has no costumes!",
          "",
          "Tip: Call addCostume() before using setTransparency().");
      return;
    }

    for (Image costume : this.costumes) {
      costume.setTransparency(transparency);
    }
  }

  /**
   * Changes the transparency for the sprite.
   *
   * @see Image#changeTransparency(double)
   * @param step a step value
   *
   * @example.preview SpriteChangeTransparency.gif
   * @example.files SpriteChangeTransparency.java
   *
   * @scratchblock change [ghost v] effect by (step)
   */
  public void changeTransparency(double step) {
    if (this.costumes.size() == 0) {
      warnOnce("changeTransparency-no-costume",
          "WARNING: changeTransparency() called but this sprite has no costumes!",
          "",
          "Tip: Call addCostume() before using changeTransparency().");
      return;
    }

    for (Image costume : this.costumes) {
      costume.changeTransparency(step);
    }
  }

  /**
   * Gets the transparency of the current costume.
   *
   * @see Image#getTransparency()
   * @return the transparency of the current costume, or 0 if there are no
   */
  public double getTransparency() {
    if (this.costumes.size() == 0)
      return 0;
    return this.costumes.get(currentCostume).getTransparency();
  }

  /**
   * Hides the sprite. The pen is not effected.
   *
   * @scratchblock hide
   *
   * @example.preview SpriteHide.gif
   * @example.files SpriteHide.java
   */
  public void hide() {
    this.show = false;
  }

  /**
   * Shows the sprite.
   *
   * @scratchblock show
   *
   * @example.preview SpriteShow.gif
   * @example.files SpriteShow.java
   */
  public void show() {
    this.show = true;
  }

  /**
   * Returns if the sprite is visible
   *
   * @return is visible
   *
   * @example.preview SpriteIsVisible.gif
   * @example.files SpriteIsVisible.java
   */
  public boolean isVisible() {
    return this.show;
  }

  /**
   * Returns the size of the sprite.
   *
   * @return size in percentage
   *
   * @scratchblock (size)
   */
  public double getSize() {
    return this.size;
  }

  /**
   * Sets the size of the sprite.
   *
   * @scratchblock set size to (percentage) %
   *
   * @param percentage a percentage [0...100]
   *
   * @example.preview SpriteSetSize.gif
   * @example.files SpriteSetSize.java
   */
  public void setSize(double percentage) {
    this.size = percentage;
    for (Image costume : this.costumes) {
      costume.setSize(percentage);
    }
  }

  /**
   * * Sets the height of the sprite.
   *
   * @param height a height in pixels
   */
  protected void setHeight(double height) {
    if (this.costumes.size() == 0) {
      warnOnce("setHeight-no-costume",
          "WARNING: setHeight() called but this sprite has no costumes!",
          "",
          "Tip: Call addCostume() before using setHeight().");
      return;
    }
    for (Image costume : this.costumes) {
      costume.setHeight((int) height);
    }
  }


  /**
   * * Sets the width of the sprite.
   *
   * @param width a width in pixels
   */
  protected void setWidth(double width) {
    if (this.costumes.size() == 0) {
      warnOnce("setWidth-no-costume",
          "WARNING: setWidth() called but this sprite has no costumes!",
          "",
          "Tip: Call addCostume() before using setWidth().");
      return;
    }
    for (Image costume : this.costumes) {
      costume.setWidth((int) width);
    }
  }


  /**
   * Changes the size of the sprite by a given percentage.
   *
   * @param amount a percentage [0...100]
   *
   * @scratchblock change size by (amount)
   */
  public void changeSize(double amount) {
    this.setSize(this.size + amount);
  }

  /**
   * Checks if the sprite is on the edge of the stage and bounces it back if it
   * is. The sprite's
   * direction is reversed when it hits the left or right border, and it is
   * reversed and rotated by
   * 180 degrees when it hits the top or bottom border. The method does nothing if
   * the hitbox is
   * disabled or if the sprite is a UI element. It also does nothing if the
   * sprite has not been added to a stage yet.
   *
   * @example.preview SpriteIfOnEdgeBounce.gif
   * @example.files SpriteIfOnEdgeBounce.java
   *
   * @scratchblock if on edge, bounce
   */
  public void ifOnEdgeBounce() {
    if (this.hitboxDisabled || this.isUI || this.stage == null)
      return;

    var h = this.getHitbox();
    var bounds = h.getBounds();

    if (h.intersects(this.stage.leftBorder)) {
      this.setDirection(-this.getDirection());
      this.setX(-this.stage.getWidth() / 2 + bounds.width() / 2);
    }
    if (h.intersects(this.stage.rightBorder)) {
      this.setDirection(-this.getDirection());
      this.setX(this.stage.getWidth() / 2 - bounds.width() / 2);
    }
    if (h.intersects(this.stage.topBorder)) {
      this.setDirection(-this.getDirection() - 180);
      this.setY(this.stage.getHeight() / 2 - bounds.height() / 2);
    }
    if (h.intersects(this.stage.bottomBorder)) {
      this.setDirection(-this.getDirection() - 180);
      this.setY(-this.stage.getHeight() / 2 + bounds.height() / 2);
    }
  }

  /**
   * Sets the rotation style for the sprite.
   *
   * @see RotationStyle
   * @param style the rotation style to be set
   *
   * @example.preview SpriteSetRotationStyle.gif
   * @example.files SpriteSetRotationStyle.java
   *
   * @scratchblock set rotation style [left-right v]
   */
  public void setRotationStyle(RotationStyle style) {
    this.rotationStyle = style;
  }

  /**
   * Sets the position of the sprite
   *
   * @scratchblock go to x: (x) y: (y)
   *
   * @param x a x coordinate
   * @param y a y coordinate
   *
   * @example.preview SpriteSetPosition.gif
   * @example.files SpriteSetPosition.java
   */
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
    this.getPen().setPosition(x, y);
  }

  /**
   * Sets the position of the sprite based on the coordinates of a given vector.
   *
   * @param v a vector
   */
  public void setPosition(Vector2 v) {
    this.setPosition(v.getX(), v.getY());
  }

  /**
   * Changes the position of the sprite by a given vector.
   *
   * @param v a vector representing the change in position
   */
  public void changePosition(Vector2 v) {
    this.setPosition(this.x + v.getX(), this.y + v.getY());
  }

  /**
   * Retrieves the current position of the sprite.
   *
   * @return A {@link Vector2} object representing the x and y coordinates of the
   *         sprite.
   */
  public Vector2 getPosition() {
    return new Vector2(x, y);
  }

  /**
   * Rotates the sprite by a certain degrees to the left.
   *
   * @scratchblock turn left (degrees) degrees
   *
   * @param degrees between 0 and 360
   *
   * @example.preview SpriteTurnLeft.gif
   * @example.files SpriteTurnLeft.java
   */
  public void turnLeft(double degrees) {
    this.setDirection(this.direction - degrees);
  }

  /**
   * Rotates the sprite by a certain degrees to the right.
   *
   * @scratchblock turn right (degrees) degrees
   *
   * @param degrees between 0 and 360
   *
   * @example.preview SpriteTurnRight.gif
   * @example.files SpriteTurnRight.java
   */
  public void turnRight(double degrees) {
    this.setDirection(this.direction + degrees);
  }

  /**
   * Sets the direction of the sprite to a given degrees. When this value is 0 the
   * sprite moves
   * up, when it is 180 it moves down.
   *
   * @scratchblock point in direction (degrees)
   *
   * @param degrees between 0 and 360
   *
   * @example.preview SpriteSetDirection.gif
   * @example.files SpriteSetDirection.java
   */
  public void setDirection(double degrees) {
    this.direction = degrees;
    if (this.direction < 0) {
      this.direction += 360;
    }
    this.direction %= 360;
  }

  /**
   * Sets the direction of the sprite to the direction of a given vector.
   *
   * @param v a vector
   */
  public void setDirection(Vector2 v) {
    this.setDirection(v.angle());
  }

  /**
   * Points the sprite in the specified direction.
   *
   * @param degrees The direction in degrees to point the sprite. 0 degrees is up,
   *                90 degrees is to the right, 180 degrees is down, and 270
   *                degrees is to the left.
   */
  public void pointInDirection(double degrees) {
    this.setDirection(degrees);
  }

  /**
   * Points the sprite in the direction of the given vector.
   *
   * @param v the target vector to point towards
   */
  public void pointInDirection(Vector2 v) {
    double angle = v.sub(this.getPosition()).angle();
    this.setDirection(90 - angle);
  }

  /**
   * Points the sprite towards the current position of the mouse pointer. This
   * method calculates the
   * angle between the sprite's current position and the mouse pointer's position,
   * then sets the
   * sprite's direction accordingly.
   *
   * @example.preview SpritePointTowardsMousePointer.gif
   * @example.files SpritePointTowardsMousePointer.java
   *
   * @scratchblock point towards [mouse-pointer v]
   */
  public void pointTowardsMousePointer() {
    var mx = this.getMouseX();
    var my = this.getMouseY();

    double angle = new Vector2(mx - this.x, my - this.y).angle();
    this.setDirection(90 - angle);
  }

  /**
   * Points the current sprite towards the specified sprite.
   *
   * @param s the sprite to point towards
   *
   * @scratchblock point towards [sprite v]
   */
  public void pointTowardsSprite(Sprite s) {
    var mx = s.getX();
    var my = s.getY();

    double angle = new Vector2(mx - this.x, my - this.y).angle();
    this.setDirection(90 - angle);
  }

  /**
   * Returns the direction of the sprite.
   *
   * @return the direction [0...360]
   *
   * @example.preview SpriteGetDirection.gif
   * @example.files SpriteGetDirection.java
   *
   * @scratchblock (direction)
   */
  public double getDirection() {
    return this.direction;
  }

  /**
   * Returns the pen of the sprite.
   *
   * @return
   *
   * @example.preview SpriteGetPen.gif
   * @example.files SpriteGetPen.java
   */
  public Pen getPen() {
    return this.pen;
  }

  /**
   * Moves the sprite towards the current rotation by the received steps.
   *
   * @scratchblock move (steps) steps
   *
   * @param steps a number of pixels
   *
   * @example.preview SpriteMove.gif
   * @example.files SpriteMove.java
   */
  public void move(double steps) {
    // convert degrees to radians
    var newX = steps * Math.cos((this.direction - 90) * Math.PI / 180) + this.x;
    var newY = steps * -Math.sin((this.direction - 90) * Math.PI / 180) + this.y;

    this.x = newX;
    this.y = newY;

    if (this.onEdgeBounce) {
      this.ifOnEdgeBounce();
    }

    this.pen.setPosition(this.x, this.y);
  }

  /**
   * Moves the sprite in the direction of the given vector. The length of the
   * vector determines how
   * move the sprite will move in this direction.
   *
   * @param v a vector
   */
  public void move(Vector2 v) {
    this.setDirection(v.angle());
    this.move(v.length());
  }

  /**
   * Returns the x coordinate of the sprite
   *
   * @return a x coordinate
   *
   * @example.preview SpriteGetX.gif
   * @example.files SpriteGetX.java
   *
   * @scratchblock (x position)
   */
  public double getX() {
    return this.x;
  }

  /**
   * Sets the x coordinate
   *
   * @scratchblock set x to (x)
   *
   * @param x a x coordinate
   *
   * @example.preview SpriteSetX.gif
   * @example.files SpriteSetX.java
   */
  public void setX(double x) {
    this.x = x;
    this.pen.setPosition(this.x, this.y);
  }

  /**
   * Changes x by a certain amount
   *
   * @scratchblock change x by (x)
   *
   * @param x number in pixels
   *
   * @example.preview SpriteChangeX.gif
   * @example.files SpriteChangeX.java
   */
  public void changeX(double x) {
    this.x += x;
    this.pen.setPosition(this.x, this.y);
  }

  /**
   * Returns the y coordinate of the sprite
   *
   * @return a y coordinate
   *
   * @example.preview SpriteGetY.gif
   * @example.files SpriteGetY.java
   *
   * @scratchblock (y position)
   */
  public double getY() {
    return this.y;
  }

  /**
   * Sets the y coordinate
   *
   * @scratchblock set y to (y)
   *
   * @param y a y coordinate
   *
   * @example.preview SpriteSetY.gif
   * @example.files SpriteSetY.java
   */
  public void setY(double y) {
    this.y = y;
    this.pen.setPosition(this.x, this.y);
  }

  /**
   * Changes y by a certain amount
   *
   * @scratchblock change y by (y)
   *
   * @param y number in pixels
   *
   * @example.preview SpriteChangeY.gif
   * @example.files SpriteChangeY.java
   */
  public void changeY(double y) {
    this.y += y;
    this.pen.setPosition(this.x, this.y);
  }

  /**
   * Return the width of the current costume or the pen size, when no costume is
   * available.
   *
   * @return the width of the sprite
   *
   * @example.preview SpriteGetWidth.gif
   * @example.files SpriteGetWidth.java
   */
  public int getWidth() {
    if (this.costumes.size() == 0)
      return (int) this.getPen().getSize();

    return this.costumes.get(this.currentCostume).getWidth();
  }

  /**
   * Return the height of the current costume or the pen size, when no costume is
   * available.
   *
   * @return the height of the sprite
   *
   * @example.preview SpriteGetHeight.gif
   * @example.files SpriteGetHeight.java
   */
  public int getHeight() {
    if (this.costumes.size() == 0)
      return (int) this.getPen().getSize();

    return this.costumes.get(this.currentCostume).getHeight();
  }

  /**
   * Return the default timer
   *
   * @return the default timer
   *
   * @scratchblock (timer)
   */
  public Timer getTimer() {
    return this.timer.get("default");
  }

  /**
   * Return a timer by name
   *
   * @return a timer
   */
  public Timer getTimer(String name) {
    // Created on first use, so that a timer never has to be declared up front.
    return this.timer.computeIfAbsent(name, n -> new Timer());
  }



  /**
   * Returns true is the mouse pointer is touching the hitbox of the sprite.
   *
   * @scratchblock &lt;touching [mouse-pointer v]?&gt;
   *
   * @return true if touching
   *
   * @example.preview SpriteIsTouchingMousePointer.gif
   * @example.files SpriteIsTouchingMousePointer.java
   */
  public boolean isTouchingMousePointer() {
    if (this.hitboxDisabled)
      return false;

    var mx = this.getMouseX();
    var my = this.getMouseY();

    return this.getHitbox().contains(mx, -my);
  }

  /**
   * Returns true if the rectangle which contains the image is outside of the
   * stage
   *
   * @return true if outside
   *
   * @example.preview SpriteIsTouchingEdge.gif
   * @example.files SpriteIsTouchingEdge.java
   *
   * @scratchblock &lt;touching [edge v]?&gt;
   */
  public boolean isTouchingEdge() {
    if (this.hitboxDisabled)
      return false;
    var h = this.getHitbox();
    return h.intersects(this.stage.topBorder)
        || h.intersects(this.stage.bottomBorder)
        || h.intersects(this.stage.leftBorder)
        || h.intersects(this.stage.rightBorder);
  }

  /**
   * Calculates the distance from the current sprite to the mouse pointer.
   *
   * @return the distance to the mouse pointer as a double.
   *
   * @example.preview SpriteDistanceToMousePointer.gif
   * @example.files SpriteDistanceToMousePointer.java
   *
   * @scratchblock (distance to [mouse-pointer v])
   */
  public double distanceToMousePointer() {
    var x2 = this.getMouseX();
    var y2 = this.getMouseY();
    var x1 = this.getX();
    var y1 = this.getY();
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  /**
   * Calculates the Euclidean distance between this sprite and another sprite.
   *
   * @param sprite the other sprite to which the distance is calculated
   * @return the distance between this sprite and the specified sprite
   *
   * @example.preview SpriteDistanceToSprite.gif
   * @example.files SpriteDistanceToSprite.java
   *
   * @scratchblock (distance to [sprite v])
   */
  public double distanceToSprite(Sprite sprite) {
    var x2 = sprite.getX();
    var y2 = sprite.getY();
    var x1 = this.getX();
    var y1 = this.getY();
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  /**
   * Sets the hitbox for the sprite using the provided points. The points should
   * be provided in
   * pairs representing the x and y coordinates.
   *
   * @param points an array of integers representing the x and y coordinates of
   *               the hitbox vertices.
   *               The length of the array should be even, with each pair of
   *               integers representing a point (x,
   *               y).
   *
   * @example.preview SpriteSetHitbox.gif
   * @example.files SpriteSetHitbox.java
   */
  public void setHitbox(double... points) {
    int l = points.length / 2;
    double[] xPoints = new double[l];
    double[] yPoints = new double[l];
    for (int i = 0; i < points.length; i += 2) {
      xPoints[i / 2] = points[i];
      yPoints[i / 2] = points[i + 1];
    }
    this.hitbox = new Hitbox(xPoints, yPoints);
  }



  /**
   * Sets the hitbox for the sprite using the specified shape.
   *
   * @param shape the shape to be used for the hitbox
   */
  public void setHitbox(Shape shape) {
    this.hitbox = new Hitbox(shape);
  }

  /**
   * Disables the hitbox for the sprite. Once the hitbox is disabled, the sprite
   * will no longer
   * detect collisions with other objects.
   */
  public void disableHitbox() {
    this.hitboxDisabled = true;
  }

  /**
   * Enables the hitbox for the sprite. This method sets the hitboxDisabled flag
   * to false, allowing
   * the sprite to interact with other objects.
   */
  public void enableHitbox() {
    this.hitboxDisabled = false;
  }

  /**
   * Returns the hitbox of the sprite based on its current costume, position, and
   * rotation. If the
   * sprite has a hitbox already defined, it updates and returns it. Otherwise, it
   * calculates a new
   * hitbox based on the sprite's dimensions and rotation.
   *
   * @return the hitbox of the sprite
   */
  public Hitbox getHitbox() {
    Image currentCostume = null;
    if (this.costumes.size() > this.getCurrentCostumeIndex()) {
      currentCostume = this.costumes.get(this.getCurrentCostumeIndex());
    }
    var costumeWidth = currentCostume != null ? currentCostume.getWidth() : this.pen.getSize();
    var costumeHeight = currentCostume != null ? currentCostume.getHeight() : this.pen.getSize();
    var spriteWidth = this.show ? costumeWidth : this.pen.getSize();
    var spriteHeight = this.show ? costumeHeight : this.pen.getSize();

    var rotation = this.direction - 90;
    if (this.rotationStyle == RotationStyle.DONT
        || this.rotationStyle == RotationStyle.LEFT_RIGHT) {
      rotation = 0;
    }

    if (this.hitbox != null) {
      this.hitbox.translateAndRotateAndResize(
          rotation,
          this.x,
          -this.y,
          this.x - spriteWidth / 2.0f,
          -this.y - spriteHeight / 2.0f,
          this.size);
      return this.hitbox;
    }

    // By default the hitbox wraps the painted pixels of the costume, not the
    // whole costume. Costumes are often drawn into a larger canvas - a standing
    // pose in a costume tall enough to also hold a jumping one - and colliding
    // with that empty space looks like a bug to whoever plays the game.
    var left = this.x - spriteWidth / 2.0;
    var top = -this.y - spriteHeight / 2.0;
    var boundsWidth = spriteWidth;
    var boundsHeight = spriteHeight;

    if (currentCostume != null && this.show
        && currentCostume.getOriginalWidth() > 0 && currentCostume.getOriginalHeight() > 0) {
      var content = currentCostume.getContentBounds();
      var scaleX = spriteWidth / (double) currentCostume.getOriginalWidth();
      var scaleY = spriteHeight / (double) currentCostume.getOriginalHeight();

      left += content[0] * scaleX;
      top += content[1] * scaleY;
      boundsWidth = content[2] * scaleX;
      boundsHeight = content[3] * scaleY;
    }

    var right = left + boundsWidth;
    var bottom = top + boundsHeight;

    var cornerTopLeft = Utils.rotateXY(left, top, this.x, -this.y, rotation);
    var cornerTopRight = Utils.rotateXY(right, top, this.x, -this.y, rotation);
    var cornerBottomLeft = Utils.rotateXY(left, bottom, this.x, -this.y, rotation);
    var cornerBottomRight = Utils.rotateXY(right, bottom, this.x, -this.y, rotation);

    double[] xPoints = new double[4];
    double[] yPoints = new double[4];
    xPoints[0] = cornerTopLeft[0];
    yPoints[0] = cornerTopLeft[1];
    xPoints[1] = cornerTopRight[0];
    yPoints[1] = cornerTopRight[1];
    xPoints[2] = cornerBottomRight[0];
    yPoints[2] = cornerBottomRight[1];
    xPoints[3] = cornerBottomLeft[0];
    yPoints[3] = cornerBottomLeft[1];

    Hitbox hitbox = new Hitbox(xPoints, yPoints);
    return hitbox;
  }

  /**
   * Checks if this sprite is touching another sprite.
   *
   * @scratchblock &lt;touching [sprite v]?&gt;
   *
   * @param sprite The sprite to check for collision with.
   * @return true if this sprite is touching the specified sprite, false
   *         otherwise. Returns false if
   *         the specified sprite is the same as this sprite, if the stage is
   *         null, if the specified
   *         sprite is null, not shown, or has its hitbox disabled.
   *
   * @example.preview SpriteIsTouchingSprite.gif
   * @example.files SpriteIsTouchingSprite.java
   */
  public boolean isTouchingSprite(Sprite sprite) {
    if (sprite == this)
      return false;
    if (stage == null)
      return false;
    if (sprite == null || !sprite.show || sprite.hitboxDisabled)
      return false;
    return this.getHitbox().intersects(sprite.getHitbox());
  }

  /**
   * Checks if this sprite is touching any sprite of the specified class type.
   *
   * @param c the class type of the sprite to check for collision
   * @return true if this sprite is touching any sprite of the specified class
   *         type, false otherwise
   */
  public boolean isTouchingSprite(Class<? extends Sprite> c) {
    if (stage == null)
      return false;
    return this.stage.sprites.stream()
        .filter(s -> !s.isUI())
        .filter(s -> s.show)
        .filter(s -> !s.hitboxDisabled)
        .filter(s -> c.isInstance(s))
        .anyMatch(s -> this.isTouchingSprite(s));
  }

  /**
   * Returns the first sprite of the specified type that is currently touching
   * this sprite.
   *
   * @param <T> the type of the sprite to check for
   * @param c   the class object of the type of sprite to check for
   * @return the first sprite of the specified type that is touching this sprite,
   *         or null if no such
   *         sprite is found
   */
  public <T extends Sprite> T getTouchingSprite(Class<T> c) {
    if (stage == null)
      return null;
    return this.stage.sprites.stream()
        .filter(s -> !s.isUI())
        .filter(s -> s.show)
        .filter(s -> !s.hitboxDisabled)
        .filter(s -> c.isInstance(s))
        .filter(s -> this.isTouchingSprite(s))
        .findFirst()
        .map(c::cast)
        .orElse(null);
  }

  /**
   * Returns a list of sprites of the specified type that are currently touching
   * this sprite.
   *
   * @param <T> the type of sprites to return
   * @param c   the class of the type of sprites to return
   * @return a list of sprites of the specified type that are touching this
   *         sprite, or null if the
   *         stage is not set
   */
  public <T extends Sprite> List<T> getTouchingSprites(Class<T> c) {
    if (stage == null)
      return null;
    return this.stage.sprites.stream()
        .filter(s -> !s.isUI())
        .filter(s -> c.isInstance(s))
        .filter(s -> this.isTouchingSprite(s))
        .map(c::cast)
        .collect(Collectors.toList());
  }

  /**
   * Returns the current x-position of the mouse cursor
   *
   * @return x-position
   *
   * @scratchblock (mouse x)
   */
  public double getMouseX() {
    if (stage == null)
      return 0;
    return this.stage.getMouseX();
  }

  /**
   * Returns the current y-position of the mouse cursor
   *
   * @return y-position
   *
   * @scratchblock (mouse y)
   */
  public double getMouseY() {
    if (this.stage == null)
      return 0;
    return this.stage.getMouseY();
  }

  /**
   * Retrieves the current position of the mouse cursor.
   *
   * @see Vector2
   * @return a Vector2 object representing the current mouse cursor position, with
   *         the x-coordinate
   *         obtained from getMouseX() and the y-coordinate obtained from
   *         getMouseY().
   *
   * @example.preview SpriteGetMouse.gif
   * @example.files SpriteGetMouse.java
   */
  public Vector2 getMouse() {
    return new Vector2(this.getMouseX(), this.getMouseY());
  }

  /**
   * Returns true is the mouse button is down
   *
   * @return mouse button down
   *
   * @example.preview SpriteIsMouseDown.gif
   * @example.files SpriteIsMouseDown.java
   *
   * @scratchblock &lt;mouse down?&gt;
   */
  public boolean isMouseDown() {
    if (this.stage == null)
      return false;
    return this.stage.isMouseDown();
  }

  /**
   * Returns true if the key is pressed
   *
   * @param keyCode a key
   * @return key pressed
   *
   * @example.preview SpriteIsKeyPressed.gif
   * @example.files SpriteIsKeyPressed.java
   *
   * @scratchblock &lt;key [space v] pressed?&gt;
   */
  public boolean isKeyPressed(KeyCode keyCode) {
    if (this.stage == null)
      return false;
    return this.stage.isKeyPressed(keyCode);
  }

  /**
   * Gets the seconds passed since the last frame.
   *
   * @return seconds since last frame
   *
   * @example.preview SpriteGetDeltaTime.gif
   * @example.files SpriteGetDeltaTime.java
   */
  public double getDeltaTime() {
    return Window.getInstance().getDeltaTime();
  }










  void keyEvent(KeyEvent e) {
    switch (e.getAction()) {
      case KeyEvent.PRESS:
        this.whenKeyPressed(KeyCode.fromCode(e.getKeyCode()));
        break;
      case KeyEvent.RELEASE:
        this.whenKeyReleased(KeyCode.fromCode(e.getKeyCode()));
        break;
    }
  }

  /**
   * This method is called when a key is pressed. Override this method to define
   * custom behavior.
   *
   * @see KeyCode
   * @param keyCode the key that was pressed
   *
   * @example.preview SpriteWhenKeyPressed.gif
   * @example.files SpriteWhenKeyPressed.java
   *
   * @scratchblock when [space v] key pressed
   */
  public void whenKeyPressed(KeyCode keyCode) {
  }

  /**
   * This method is called when a key is released. Override this method to define
   * custom behavior.
   *
   * @see KeyCode
   * @param keyCode the key that was released
   */
  public void whenKeyReleased(KeyCode keyCode) {
  }

  /**
   * Handles mouse events. Override this method to define custom behavior.
   *
   * @param e the MouseEvent object containing details about the mouse event
   */
  void mouseEvent(MouseEvent e) {
  }

  /**
   * This method is called when the mouse is moved. Override this method to define
   * custom behavior.
   *
   * @param x The x-coordinate of the mouse pointer.
   * @param y The y-coordinate of the mouse pointer.
   *
   * @example.preview SpriteWhenMouseMoved.gif
   * @example.files SpriteWhenMouseMoved.java
   */
  public void whenMouseMoved(double x, double y) {
  }

  /**
   * This method is called when a mouse click event occurs. Override this method
   * to define custom
   * behavior.
   *
   * @param mouseCode The code representing the mouse button that was clicked.
   */
  public void whenMouseClicked(MouseCode mouseCode) {
  }

  /**
   * This method is called when the sprite is clicked. Override this method to
   * define custom
   * behavior for the sprite when it is clicked.
   *
   * @example.preview SpriteWhenClicked.gif
   * @example.files SpriteWhenClicked.java
   *
   * @scratchblock when this sprite clicked
   */
  public void whenClicked() {
  }

  /**
   * Moves the sprite to a random position within the boundaries of the stage. The
   * new position is
   * determined by generating random coordinates within the width and height of
   * the stage.
   *
   * @scratchblock go to [random position v]
   */
  public void goToRandomPosition() {
    this.setPosition(
        Random.randomInt(-this.stage.getWidth() / 2, this.stage.getWidth() / 2),
        Random.randomInt(-this.stage.getHeight() / 2, this.stage.getHeight() / 2));
  }

  /**
   * Moves the sprite to the current position of the mouse pointer. This method
   * updates the sprite's
   * position to the coordinates of the mouse cursor.
   *
   * @scratchblock go to [mouse-pointer v]
   */
  public void goToMousePointer() {
    this.setPosition(this.getMouseX(), this.getMouseY());
  }

  /**
   * Moves this sprite to the position of the specified sprite.
   *
   * @param sprite the sprite to move to
   *
   * @scratchblock go to [sprite v]
   */
  public void goToSprite(Sprite sprite) {
    this.setPosition(sprite.getX(), sprite.getY());
  }

  /**
   * Moves this sprite to the front layer of the stage. If the sprite is not part
   * of any stage, the
   * method does nothing.
   *
   * @example.preview SpriteGoToFrontLayer.gif
   * @example.files SpriteGoToFrontLayer.java
   *
   * @scratchblock go to [front v] layer
   */
  public void goToFrontLayer() {
    if (stage == null) {
      warnOnce("goToFrontLayer",
          "WARNING: goToFrontLayer() called but this sprite is not on a stage!",
          "",
          "Tip: Add the sprite to a stage before changing its layer.");
      return;
    }
    this.stage.goToFrontLayer(this);
  }

  /**
   * Moves the sprite to the back layer of the stage. If the sprite is not
   * associated with any
   * stage, the method returns without performing any action.
   *
   * @example.preview SpriteGoToBackLayer.gif
   * @example.files SpriteGoToBackLayer.java
   *
   * @scratchblock go to [back v] layer
   */
  public void goToBackLayer() {
    if (stage == null) {
      warnOnce("goToBackLayer",
          "WARNING: goToBackLayer() called but this sprite is not on a stage!",
          "",
          "Tip: Add the sprite to a stage before changing its layer.");
      return;
    }
    this.stage.goToBackLayer(this);
  }

  /**
   * Moves the sprite forward by a specified number of layers within its stage.
   *
   * @param number the number of layers to move the sprite forward
   *
   * @example.preview SpriteGoLayersForwards.gif
   * @example.files SpriteGoLayersForwards.java
   *
   * @scratchblock go [forward v] (number) layers
   */
  public void goLayersForwards(int number) {
    if (stage == null) {
      warnOnce("goLayersForwards",
          "WARNING: goLayersForwards() called but this sprite is not on a stage!",
          "",
          "Tip: Add the sprite to a stage before changing its layer.");
      return;
    }
    this.stage.goLayersForwards(this, number);
  }

  /**
   * Moves the sprite backwards by a specified number of layers in the stage. If
   * the sprite is not
   * part of a stage, the method does nothing.
   *
   * @param number the number of layers to move the sprite backwards
   *
   * @example.preview SpriteGoLayersBackwards.gif
   * @example.files SpriteGoLayersBackwards.java
   *
   * @scratchblock go [backward v] (number) layers
   */
  public void goLayersBackwards(int number) {
    if (stage == null) {
      warnOnce("goLayersBackwards",
          "WARNING: goLayersBackwards() called but this sprite is not on a stage!",
          "",
          "Tip: Add the sprite to a stage before changing its layer.");
      return;
    }
    this.stage.goLayersBackwards(this, number);
  }

  /**
   * This method is called when the backdrop switches to the specified name.
   * Override this method to
   * define custom behavior.
   *
   * @param name the name of the backdrop to switch to
   *
   * @example.preview SpriteWhenBackdropSwitches.gif
   * @example.files SpriteWhenBackdropSwitches.java
   *
   * @scratchblock when backdrop switches to [name v]
   */
  public void whenBackdropSwitches(String name) {
  }

  /**
   * Returns a random integer between the specified range.
   *
   * @param from the lower bound of the range (inclusive)
   * @param to   the upper bound of the range (exclusive)
   * @return a random integer between the specified range
   *
   * @example.preview SpritePickRandom.gif
   * @example.files SpritePickRandom.java
   *
   * @scratchblock (pick random (from) to (to))
   */
  public int pickRandom(int from, final int to) {
    if (from == to) {
      return from;
    }
    if (to < from) {
      return to + (int) (Math.random() * (from - to + 1));
    }
    return from + (int) (Math.random() * (to - from + 1));
  }

  /**
   * Retrieves the text associated with this sprite.
   *
   * @return the text associated with this sprite
   */
  public Text getText() {
    return this.text;
  }

  /**
   * Displays a thought bubble with the specified text.
   *
   * @scratchblock think [text]
   *
   * @param text The text to be displayed in the thought bubble.
   *
   * @example.preview SpriteThink.gif
   * @example.files SpriteThink.java
   */
  public void think(String text) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text);
  }

  /**
   * Displays a thought bubble with the specified text for a given duration.
   *
   * @param text   The text to be displayed in the thought bubble.
   * @param millis The duration in milliseconds for which the thought bubble will
   *               be displayed.
   *
   * @scratchblock think [text] for (millis) seconds
   */
  public void think(String text, final int millis) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text, millis);
  }

  /**
   * Makes the sprite display a speech bubble with the specified text.
   *
   * @scratchblock say [text]
   *
   * @param text The text to be displayed in the speech bubble.
   *
   * @example.preview SpriteSay.gif
   * @example.files SpriteSay.java
   */
  public void say(String text) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text);
  }

  /**
   * Displays a text message for a specified duration.
   *
   * @param text   The message to be displayed.
   * @param millis The duration in milliseconds for which the message will be
   *               displayed.
   *
   * @scratchblock say [text] for (millis) seconds
   */
  public void say(String text, final int millis) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text, millis);
  }

  /**
   * Sets how loud every sound of this sprite plays.
   *
   * @param percent 0 for silent, 100 for full volume
   *
   * @scratchblock set volume to (percent) %
   */
  public void setVolume(double percent) {
    this.volume = Math.max(0, Math.min(100, percent));
    for (Sound sound : this.sounds) {
      sound.setVolume(this.volume / 100.0);
    }
  }

  /**
   * Makes every sound of this sprite louder or quieter.
   *
   * @param step how much to add to the volume, in percent
   *
   * @scratchblock change volume by (step)
   */
  public void changeVolume(double step) {
    this.setVolume(this.volume + step);
  }

  /**
   * Returns how loud the sounds of this sprite play.
   *
   * @return the volume, from 0 to 100
   *
   * @scratchblock (volume)
   */
  public double getVolume() {
    return this.volume;
  }



  /**
   * Slides the sprite to a place over the given time, instead of jumping there.
   *
   * <p>
   * Unlike Scratch this does not hold up the sprite: `run()` keeps being called
   * while the sprite is on its way. Use {@link #isGliding()} to tell whether it
   * has arrived.
   *
   * @param seconds how long the trip should take
   * @param x       where to end up
   * @param y       where to end up
   *
   * @scratchblock glide (seconds) secs to x: (x) y: (y)
   */
  public void glide(double seconds, double x, double y) {
    if (seconds <= 0) {
      this.setPosition(x, y);
      this.glideElapsed = -1;
      return;
    }
    this.glideFromX = this.x;
    this.glideFromY = this.y;
    this.glideToX = x;
    this.glideToY = y;
    this.glideMillis = seconds * 1000;
    this.glideElapsed = 0;
  }

  /**
   * Checks whether the sprite is still on its way to a place it was told to
   * glide to.
   *
   * @return true while it is still moving
   */
  public boolean isGliding() {
    return this.glideElapsed >= 0;
  }

  /** Moves a gliding sprite a little further along; called once per frame. */
  void stepGlide(double deltaSeconds) {
    if (this.glideElapsed < 0) {
      return;
    }
    this.glideElapsed += deltaSeconds * 1000;
    var progress = Math.min(1, this.glideElapsed / this.glideMillis);
    this.setPosition(
        this.glideFromX + (this.glideToX - this.glideFromX) * progress,
        this.glideFromY + (this.glideToY - this.glideFromY) * progress);
    if (progress >= 1) {
      this.glideElapsed = -1;
    }
  }

  /**
   * Asks a question and waits for an answer to be typed in. The question appears
   * at the top of the stage.
   *
   * <p>
   * Unlike Scratch, this does not pause the sprite. `run()` keeps being called
   * while the question is on screen, so check {@link #isAsking()} or wait for
   * {@link #getAnswer()} to change.
   *
   * @param question the question to show
   *
   * @scratchblock ask [question] and wait
   */
  public void ask(String question) {
    if (this.stage == null) {
      this.warnOnce("ask-no-stage",
          "WARNING: ask() called but this sprite is not on a stage!",
          "",
          "Tip: Add the sprite to a stage before asking a question.");
      return;
    }
    this.stage.ask(question);
  }

  /**
   * Returns the last answer that was typed in.
   *
   * @return the answer, or an empty string if nothing has been answered yet
   *
   * @scratchblock (answer)
   */
  public String getAnswer() {
    return this.stage == null ? "" : this.stage.getAnswer();
  }

  /**
   * Checks whether a question is on screen and still waiting for an answer.
   *
   * @return true while a question is waiting
   */
  public boolean isAsking() {
    return this.stage != null && this.stage.isAsking();
  }

  /**
   * Broadcasts a message to all sprites in the stage except the current sprite.
   * If the stage is not
   * set, the method returns immediately.

   * @scratchblock broadcast [message v]
   *
   * @param message The message to broadcast to other sprites.
   *
   * @example.preview SpriteBroadcast.gif
   * @example.files SpriteBroadcast.java
   */
  public void broadcast(String message) {
    if (stage == null) {
      warnOnce("broadcast",
          "WARNING: broadcast() called but this sprite is not on a stage!",
          "",
          "Tip: Don't call broadcast() in the constructor.",
          "     Add the sprite to a stage first, then broadcast.");
      return;
    }
    this.stage.sprites.stream().filter(s -> s != this).forEach(s -> s.whenIReceive(message));
    this.stage.whenIReceive(message);
  }


  /**
   * This method is called when a message is received. Override this method to
   * define custom
   * behavior.
   *
   * @see Sprite#broadcast(String)
   * @param message The message that is received.
   *
   * @scratchblock when I receive [message v]
   */
  public void whenIReceive(String message) {
  }


  /**
   * Stamps the current sprite to the background. A stamp is a non interactive
   * version of the sprite.
   *
   * @example.preview SpriteStamp.gif
   * @example.files SpriteStamp.java
   *
   * @scratchblock stamp
   */
  public void stamp() {
    this.stampToBackground();
  }

  /**
   * Stamps the sprite onto one of the stage's layers. A stamp is a picture of
   * the sprite that stays where it is put, and is not interactive.
   *
   * <p>
   * Example usage:
   *
   * <pre>{@code
   * this.stamp(Layer.UI);
   * }</pre>
   *
   * @param layer which layer to stamp onto
   */
  public void stamp(Layer layer) {
    if (layer == null) {
      return;
    }
    switch (layer) {
      case BACKGROUND -> this.stampToBackground();
      case FOREGROUND -> this.stampToForeground();
      case UI -> this.stampToUI();
    }
  }

  /**
   * Stamps the current sprite to the background. A stamp is a non interactive
   * version of the sprite.
   *
   */
  void stampToBackground() {
    if (this.costumes.size() > 0) {
      this.stage.addStampsToBackground(this.getStamp());
    }
  }

  /**
   * Stamps the current sprite to the ui. A stamp is a non interactive version of
   * the sprite.
   *
   */
  void stampToUI() {
    if (this.costumes.size() > 0) {
      this.stage.addStampsToUI(this.getStamp());
    }
  }

  /**
   * Stamps the current sprite to the foreground. A stamp is a non interactive
   * version of the sprite.
   *
   */
  void stampToForeground() {
    if (this.costumes.size() > 0) {
      this.stage.addStampsToForeground(this.getStamp());
    }
  }

  /**
   * Sets the UI status of the sprite.
   *
   * @param isUI A boolean value indicating whether the sprite is part of the UI.
   */
  protected void setUI(boolean isUI) {
    this.isUI = isUI;
  }

  /**
   * Checks if the sprite is part of the user interface.
   *
   * @return true if the sprite is part of the user interface, false otherwise.
   */
  protected boolean isUI() {
    return this.isUI;
  }

  /**
   * This method is intended to be overridden by subclasses to define the behavior
   * of the sprite
   * when it is run. By default, this method does nothing.
   *
   * <p>
   * It is called every frame.
   *
   * @example.preview SpriteRun.gif
   * @example.files SpriteRun.java
   */
  public void run() {
  }

  protected void addedToStage(Stage stage) {
    this.stage = stage;
    this.pen.addedToStage(stage);
    this.text.addedToStage(stage);
    this.whenAddedToStage();
  }

  protected void removedFromStage(Stage stage) {
    this.pen.removedFromStage(stage);
    this.text.removedFromStage(stage);
    this.stage = null;
    this.whenRemovedFromStage();
  }

  /** Draws the sprite if it is not hidden. */
  protected void draw(PGraphics buffer) {
    if (this.stage == null)
      return;
    if (!this.show) {
      return;
    }
    if (this.costumes.isEmpty()) {
      // A sprite given a collision shape but no costume is an invisible wall or
      // trigger, which is a normal thing to build. Only a sprite with neither is
      // likely to be a forgotten addCostume().
      if (this.hitbox != null) {
        return;
      }
      // Drawing nothing at all looks exactly like a broken program, so say what
      // is wrong and put a marker where the sprite would be.
      this.warnOnce("no-costume",
          "WARNING: " + this.getClass().getSimpleName() + " has no costume, so there is nothing to draw!",
          "",
          "Tip: Call addCostume() in the constructor, for example",
          "     this.addCostume(\"bunny1_stand\");",
          "     All built-in costumes are listed at https://scratch4j.openpatch.org/sprites");
      this.drawMissingCostume(buffer);
      return;
    }
    var shader = this.shaders.getCurrent();
    this.costumes
        .get(this.currentCostume)
        .draw(buffer, this.size, this.direction, this.x, this.y, this.rotationStyle, shader);
  }

  /** A question mark where a sprite without a costume would have been. */
  private void drawMissingCostume(PGraphics buffer) {
    var side = 40 * this.size / 100.0;
    buffer.push();
    buffer.translate((float) this.x, (float) -this.y);
    buffer.rectMode(PConstants.CENTER);
    buffer.stroke(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
    buffer.strokeWeight(2);
    buffer.noFill();
    buffer.rect(0, 0, (float) side, (float) side);
    buffer.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
    buffer.textAlign(PConstants.CENTER, PConstants.CENTER);
    buffer.textSize((float) (side * 0.6));
    buffer.text("?", 0, 0);
    buffer.pop();
  }

  /**
   * Draws debug information for the sprite. This includes the hitbox and the
   * current costume. The
   * hitbox is drawn if it is not disabled and the sprite is not a UI element. The
   * current costume
   * is drawn if there are costumes available and the sprite is set to be shown.
   */
  protected void drawDebug(PGraphics buffer) {
    if (!this.hitboxDisabled) {
      this.getHitbox().drawDebug(buffer);
    }
    if (this.costumes.size() > 0 && this.show) {
      this.costumes
          .get(this.currentCostume)
          .drawDebug(buffer, this.size, this.direction, this.x, this.y, this.rotationStyle);
    }
  }

  private Stamp getStamp() {
    var stamp = new Stamp(
        this.costumes.get(this.currentCostume),
        this.direction,
        this.x,
        this.y,
        this.rotationStyle);

    return stamp;
  }

  /**
   * Creates a clone of the current sprite. The cloned sprite will have the same
   * properties as the
   * original sprite, including its costumes, position, direction, and pen.
   *
   * @return a new Sprite object that is a clone of the current sprite
   *
   * @scratchblock create clone of [myself v]
   */
  public Sprite clone() {
    return new Sprite(this);
  }
}
