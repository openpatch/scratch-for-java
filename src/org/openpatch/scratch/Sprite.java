package org.openpatch.scratch;

import java.awt.Shape;
import java.util.AbstractMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.openpatch.scratch.extensions.color.Color;
import org.openpatch.scratch.extensions.hitbox.Hitbox;
import org.openpatch.scratch.extensions.math.Random;
import org.openpatch.scratch.extensions.math.Utils;
import org.openpatch.scratch.extensions.math.Vector2;
import org.openpatch.scratch.extensions.pen.Pen;
import org.openpatch.scratch.extensions.shader.Shader;
import org.openpatch.scratch.extensions.text.Text;
import org.openpatch.scratch.extensions.text.TextStyle;
import org.openpatch.scratch.extensions.timer.Timer;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Sound;
import org.openpatch.scratch.internal.Stamp;
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
 * <pre>{@code
 * Sprite sprite = new Sprite();
 * sprite.addCostume("costume1", "path/to/image.png");
 * sprite.addSound("sound1", "path/to/sound.wav");
 * sprite.setPosition(100, 200);
 * sprite.move(10);
 * sprite.turnRight(90);
 * sprite.say("Hello, world!");
 * }</pre>
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
 * @see RotationStyle
 */
public class Sprite {

  private List<Image> costumes = new CopyOnWriteArrayList<>();
  private int currentCostume = 0;
  private List<Sound> sounds = new CopyOnWriteArrayList<>();
  private List<Shader> shaders = new CopyOnWriteArrayList<>();
  private int currentShader = 0;
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

  public Sprite() {
    this.pen = new Pen(this);
    this.timer = new ConcurrentHashMap<>();
    this.text = new Text(this);
    this.x = 0;
    this.y = 0;
    this.timer.put("default", new Timer());
  }

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
    this.shaders = new CopyOnWriteArrayList<>();
    for (Shader shader : s.shaders) {
      this.shaders.add(new Shader(shader));
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
    this.hitbox = s.hitbox;
    this.hitboxDisabled = s.hitboxDisabled;
    this.text = new Text(s.text);
  }

  /**
   * This method is called when the sprite is added to the stage. Override this
   * method to define
   * custom behavior when the sprite is added to the stage.
   */
  public void whenAddedToStage() {
  }

  /**
   * This method is called when the sprite is added to the stage. Override this
   * method to define
   * custom behavior when the sprite is added to the stage.
   *
   * @param stage The stage to which the sprite is added.
   */
  public void whenAddedToStage(Stage stage) {
  }

  /**
   * This method is called when the sprite is removed from the stage. Override
   * this method to define
   * custom behavior when the sprite is removed.
   */
  public void whenRemovedFromStage() {
  }

  /**
   * This method is called when the sprite is removed from the stage. Override
   * this method to define
   * custom behavior when the sprite is removed.
   *
   * @param stage The stage from which the sprite is removed from.
   */
  public void whenRemovedFromStage(Stage stage) {
  }

  /** Removes this sprite from its current stage. */
  public void remove() {
    if (this.stage != null) {
      this.stage.remove(this);
    }
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
   * Adds a new shader to the sprite. If a shader with the received name already
   * exists, this method
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
   * Add a costume to the sprite. If a costume with the received name already
   * exists do nothing.
   *
   * @param name      a unique name
   * @param imagePath a image path
   */
  public void addCostume(String name, final String imagePath) {
    for (Image costume : this.costumes) {
      if (costume.getName().equals(name)) {
        return;
      }
    }

    Image costume = new Image(name, imagePath);
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
   * index in the spritesheet.
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
   * Switch to a costume by name.
   *
   * @param name the name of a costume
   */
  public void switchCostume(String name) {
    for (int i = 0; i < this.costumes.size(); i++) {
      Image costume = this.costumes.get(i);
      if (costume.getName().equals(name)) {
        this.currentCostume = i;
        return;
      }
    }
  }

  /**
   * Switches the current costume of the sprite to the costume at the specified
   * index.
   *
   * @param index The index of the costume to switch to.
   */
  public void switchCostume(double index) {
    this.currentCostume = (int) index % this.costumes.size();
  }

  /** Switch to the next costume. */
  public void nextCostume() {
    this.currentCostume = (this.currentCostume + 1) % this.costumes.size();
  }

  /** Switch to the next costume. */
  public void previousCostume() {
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
   */
  public int getCurrentCostumeIndex() {
    return this.currentCostume;
  }

  /**
   * Add a sound to the sprite. If a sound with the received name already exists
   * do nothing.
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
   * Remove a sound from the sprite.
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

  /** Stops the playing of all sounds of the sprite. */
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
    if (this.costumes.size() == 0)
      return;

    for (Image costume : this.costumes) {
      costume.setTint(r, g, b);
    }
  }

  /**
   * Sets the tint for the sprite with a hue.
   *
   * @see Image#setTint(double)
   * @param h a hue value [0...255]
   */
  public void setTint(double h) {
    if (this.costumes.size() == 0)
      return;

    for (Image costume : this.costumes) {
      costume.setTint(h);
    }
  }

  /**
   * Changes the tint for the sprite by a step.
   *
   * @param step a step value
   */
  public void changeTint(double step) {
    if (this.costumes.size() == 0)
      return;

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
   */
  public void setTransparency(double transparency) {
    if (this.costumes.size() == 0)
      return;

    for (Image costume : this.costumes) {
      costume.setTransparency(transparency);
    }
  }

  /**
   * Changes the transparency for the sprite.
   *
   * @see Image#changeTransparency(double)
   * @param step a step value
   */
  public void changeTransparency(double step) {
    if (this.costumes.size() == 0)
      return;

    for (Image costume : this.costumes) {
      costume.changeTransparency(step);
    }
  }

  public double getTransparency() {
    if (this.costumes.size() == 0)
      return 0;
    return this.costumes.get(currentCostume).getTransparency();
  }

  /** Hides the sprite. The pen is not effected. */
  public void hide() {
    this.show = false;
  }

  /** Shows the sprite. */
  public void show() {
    this.show = true;
  }

  /**
   * Returns if the sprite is visible
   *
   * @return is visible
   */
  public boolean isVisible() {
    return this.show;
  }

  /**
   * Returns the size of the sprite.
   *
   * @return size in percentage
   */
  public double getSize() {
    return this.size;
  }

  /**
   * Sets the size of the sprite.
   *
   * @param percentage a percentage [0...100]
   */
  public void setSize(double percentage) {
    this.size = percentage;
    for (Image costume : this.costumes) {
      costume.setSize(percentage);
    }
  }

  /**
   * Changes the size of the sprite by a given percentage.
   *
   * @param amount a percentage [0...100]
   */
  public void changeSize(double amount) {
    this.setSize(this.size + amount);
  }

  /**
   * Sets if the sprite should bounce when hitting the edge of the screen. This
   * method is for making
   * is attribute perment.
   *
   * @param b true if the sprite should bounce
   */
  public void setOnEdgeBounce(boolean b) {
    this.onEdgeBounce = b;
  }

  /**
   * Checks if the sprite is on the edge of the stage and bounces it back if it
   * is. The sprite's
   * direction is reversed when it hits the left or right border, and it is
   * reversed and rotated by
   * 180 degrees when it hits the top or bottom border. The method does nothing if
   * the hitbox is
   * disabled or if the sprite is a UI element.
   */
  public void ifOnEdgeBounce() {
    if (this.hitboxDisabled || this.isUI)
      return;

    var h = this.getHitbox();
    var bounds = h.getBounds();

    if (h.intersects(this.stage.leftBorder)) {
      this.setDirection(-this.getDirection());
      this.setX(-this.stage.getWidth() / 2 + bounds.getWidth() / 2);
    }
    if (h.intersects(this.stage.rightBorder)) {
      this.setDirection(-this.getDirection());
      this.setX(this.stage.getWidth() / 2 - bounds.getWidth() / 2);
    }
    if (h.intersects(this.stage.topBorder)) {
      this.setDirection(-this.getDirection() - 180);
      this.setY(this.stage.getHeight() / 2 - bounds.getHeight() / 2);
    }
    if (h.intersects(this.stage.bottomBorder)) {
      this.setDirection(-this.getDirection() - 180);
      this.setY(-this.stage.getHeight() / 2 + bounds.getHeight() / 2);
    }
  }

  /**
   * Sets the rotation style for the sprite.
   *
   * @see RotationStyle
   * @param style the rotation style to be set
   */
  public void setRotationStyle(RotationStyle style) {
    this.rotationStyle = style;
  }

  /**
   * Sets the position of the sprite
   *
   * @param x a x coordinate
   * @param y a y coordinate
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
   * @param degrees between 0 and 360
   */
  public void turnLeft(double degrees) {
    this.setDirection(this.direction - degrees);
  }

  /**
   * Rotates the sprite by a certain degrees to the right.
   *
   * @param degrees between 0 and 360
   */
  public void turnRight(double degrees) {
    this.setDirection(this.direction + degrees);
  }

  /**
   * Sets the direction of the sprite to a given degrees. When this value is 0 the
   * sprite move
   * right, when it is 180 is moves to the left.
   *
   * @param degrees between 0 and 360
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
   * @param degrees The direction in degrees to point the sprite. 0 degrees is to
   *                the right, 90
   *                degrees is up, 180 degrees is to the left, and 270 degrees is
   *                down.
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
   */
  public double getDirection() {
    return this.direction;
  }

  /**
   * Returns the pen of the sprite.
   *
   * @return
   */
  public Pen getPen() {
    return this.pen;
  }

  /**
   * Moves the sprite towards the current rotation by the received steps.
   *
   * @param steps a number of pixels
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
   */
  public double getX() {
    return this.x;
  }

  /**
   * Sets the x coordinate
   *
   * @param x a x coordinate
   */
  public void setX(double x) {
    this.x = x;
    this.pen.setPosition(this.x, this.y);
  }

  /**
   * Changes x by a certain amount
   *
   * @param x number in pixels
   */
  public void changeX(double x) {
    this.x += x;
    this.pen.setPosition(this.x, this.y);
  }

  /**
   * Returns the y coordinate of the sprite
   *
   * @return a y coordinate
   */
  public double getY() {
    return this.y;
  }

  /**
   * Sets the y coordinate
   *
   * @param y a y coordinate
   */
  public void setY(double y) {
    this.y = y;
    this.pen.setPosition(this.x, this.y);
  }

  /**
   * Changes y by a certain amount
   *
   * @param y number in pixels
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

  /**
   * Returns true is the mouse pointer is touching the hitbox of the sprite.
   *
   * @return true if touching
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
   */
  public void setHitbox(int... points) {
    int l = points.length / 2;
    int[] xPoints = new int[l];
    int[] yPoints = new int[l];
    for (int i = 0; i < points.length; i += 2) {
      xPoints[i / 2] = points[i];
      yPoints[i / 2] = points[i + 1];
    }
    this.hitbox = new Hitbox(xPoints, yPoints);
  }

  /**
   * Sets the hitbox for the sprite using the provided x and y coordinates.
   *
   * @param xPoints an array of x coordinates for the hitbox
   * @param yPoints an array of y coordinates for the hitbox
   */
  public void setHitbox(int[] xPoints, final int[] yPoints) {
    this.hitbox = new Hitbox(xPoints, yPoints);
  }

  /**
   * Sets the hitbox for the sprite using the provided Hitbox object.
   *
   * @param hitbox the Hitbox object to set
   */
  public void setHitbox(Hitbox hitbox) {
    this.hitbox = hitbox;
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

    var cornerTopLeft = Utils.rotateXY(
        this.x - spriteWidth / 2.0f, -this.y - spriteHeight / 2.0f, this.x, -this.y, rotation);
    var cornerTopRight = Utils.rotateXY(
        this.x + spriteWidth / 2.0f, -this.y - spriteHeight / 2.0f, this.x, -this.y, rotation);
    var cornerBottomLeft = Utils.rotateXY(
        this.x - spriteWidth / 2.0f, -this.y + spriteHeight / 2.0f, this.x, -this.y, rotation);
    var cornerBottomRight = Utils.rotateXY(
        this.x + spriteWidth / 2.0f, -this.y + spriteHeight / 2.0f, this.x, -this.y, rotation);

    int[] xPoints = new int[4];
    int[] yPoints = new int[4];
    xPoints[0] = (int) Math.round(cornerTopLeft[0]);
    yPoints[0] = (int) Math.round(cornerTopLeft[1]);
    xPoints[1] = (int) Math.round(cornerTopRight[0]);
    yPoints[1] = (int) Math.round(cornerTopRight[1]);
    xPoints[2] = (int) Math.round(cornerBottomRight[0]);
    yPoints[2] = (int) Math.round(cornerBottomRight[1]);
    xPoints[3] = (int) Math.round(cornerBottomLeft[0]);
    yPoints[3] = (int) Math.round(cornerBottomLeft[1]);

    Hitbox hitbox = new Hitbox(xPoints, yPoints);
    return hitbox;
  }

  /**
   * Checks if this sprite is touching another sprite.
   *
   * @param sprite The sprite to check for collision with.
   * @return true if this sprite is touching the specified sprite, false
   *         otherwise. Returns false if
   *         the specified sprite is the same as this sprite, if the stage is
   *         null, if the specified
   *         sprite is null, not shown, or has its hitbox disabled.
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
   */
  public Vector2 getMouse() {
    return new Vector2(this.getMouseX(), this.getMouseY());
  }

  /**
   * Returns true is the mouse button is down
   *
   * @return mouse button down
   */
  public boolean isMouseDown() {
    if (this.stage == null)
      return false;
    return this.stage.isMouseDown();
  }

  /**
   * Returns true if the key is pressed
   *
   * @param keyCode a key code
   * @return key pressed
   */
  public boolean isKeyPressed(int keyCode) {
    if (this.stage == null)
      return false;
    return this.stage.isKeyPressed(keyCode);
  }

  /**
   * Gets the seconds passed since the last frame.
   *
   * @return seconds since last frame
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
    if (stage == null)
      return 0;
    return this.stage.getCurrentYear();
  }

  /**
   * Returns the current month
   *
   * @return current month
   */
  public int getCurrentMonth() {
    if (stage == null)
      return 0;
    return this.stage.getCurrentMonth();
  }

  /**
   * Returns the current day of the month
   *
   * @return current day of the month
   */
  public int getCurrentDay() {
    if (stage == null)
      return 0;
    return this.stage.getCurrentDay();
  }

  /**
   * Returns the current day of the week
   *
   * @return current day of the week
   */
  public int getCurrentDayOfWeek() {
    if (stage == null)
      return 0;
    return this.stage.getCurrentDayOfWeek();
  }

  /**
   * Returns the current hour
   *
   * @return current hour
   */
  public int getCurrentHour() {
    if (stage == null)
      return 0;
    return this.stage.getCurrentHour();
  }

  /**
   * Returns the current minute
   *
   * @return current minute
   */
  public int getCurrentMinute() {
    if (stage == null)
      return 0;
    return this.stage.getCurrentMinute();
  }

  /**
   * Returns the current second
   *
   * @return current second
   */
  public int getCurrentSecond() {
    if (stage == null)
      return 0;
    return this.stage.getCurrentSecond();
  }

  /**
   * Returns the current millisecond
   *
   * @return current millisecond
   */
  public int getCurrentMillisecond() {
    if (stage == null)
      return 0;
    return this.stage.getCurrentMillisecond();
  }

  /**
   * Returns the days since 2010/01/01
   *
   * @return days since 2010/01/01
   */
  public int getDaysSince2000() {
    if (stage == null)
      return 0;
    return this.stage.getDaysSince2000();
  }

  public void keyEvent(KeyEvent e) {
    switch (e.getAction()) {
      case KeyEvent.PRESS:
        this.whenKeyPressed(e.getKeyCode());
        break;
      case KeyEvent.RELEASE:
        this.whenKeyReleased(e.getKeyCode());
        break;
    }
  }

  /**
   * This method is called when a key is pressed. Override this method to define
   * custom behavior.
   *
   * @see KeyCode
   * @param keyCode the code of the key that was pressed
   */
  public void whenKeyPressed(int keyCode) {
  }

  /**
   * This method is called when a key is released. Override this method to define
   * custom behavior.
   *
   * @see KeyCode
   * @param keyCode the code of the key that was released
   */
  public void whenKeyReleased(int keyCode) {
  }

  /**
   * Handles mouse events. Override this method to define custom behavior.
   *
   * @param e the MouseEvent object containing details about the mouse event
   */
  public void mouseEvent(MouseEvent e) {
  }

  /**
   * This method is called when the mouse is moved. Override this method to define
   * custom behavior.
   *
   * @param x The x-coordinate of the mouse pointer.
   * @param y The y-coordinate of the mouse pointer.
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
   */
  public void whenClicked() {
  }

  /**
   * Moves the sprite to a random position within the boundaries of the stage. The
   * new position is
   * determined by generating random coordinates within the width and height of
   * the stage.
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
   */
  public void goToMousePointer() {
    this.setPosition(this.getMouseX(), this.getMouseY());
  }

  /**
   * Moves this sprite to the position of the specified sprite.
   *
   * @param sprite the sprite to move to
   */
  public void goToSprite(Sprite sprite) {
    this.setPosition(sprite.getX(), sprite.getY());
  }

  /**
   * Moves this sprite to the front layer of the stage. If the sprite is not part
   * of any stage, the
   * method does nothing.
   */
  public void goToFrontLayer() {
    if (stage == null)
      return;
    this.stage.goToFrontLayer(this);
  }

  /**
   * Moves the sprite to the back layer of the stage. If the sprite is not
   * associated with any
   * stage, the method returns without performing any action.
   */
  public void goToBackLayer() {
    if (stage == null)
      return;
    this.stage.goToBackLayer(this);
  }

  /**
   * Moves the sprite forward by a specified number of layers within its stage.
   *
   * @param number the number of layers to move the sprite forward
   */
  public void goLayersForwards(int number) {
    if (stage == null)
      return;
    this.stage.goLayersForwards(this, number);
  }

  /**
   * Moves the sprite backwards by a specified number of layers in the stage. If
   * the sprite is not
   * part of a stage, the method does nothing.
   *
   * @param number the number of layers to move the sprite backwards
   */
  public void goLayersBackwards(int number) {
    if (stage == null)
      return;
    this.stage.goLayersBackwards(this, number);
  }

  /**
   * This method is called when the backdrop switches to the specified name.
   * Override this method to
   * define custom behavior.
   *
   * @param name the name of the backdrop to switch to
   */
  public void whenBackdropSwitches(String name) {
  }

  /**
   * Returns a random integer between the specified range.
   *
   * @param from the lower bound of the range (inclusive)
   * @param to   the upper bound of the range (exclusive)
   * @return a random integer between the specified range
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
   * @param text The text to be displayed in the thought bubble.
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
   */
  public void think(String text, final int millis) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text, millis);
  }

  /**
   * Makes the sprite display a speech bubble with the specified text.
   *
   * @param text The text to be displayed in the speech bubble.
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
   */
  public void say(String text, final int millis) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text, millis);
  }

  /**
   * Broadcasts a message to all sprites in the stage except the current sprite.
   * If the stage is not
   * set, the method returns immediately.
   *
   * @param message The message to broadcast to other sprites.
   */
  public void broadcast(String message) {
    if (stage == null)
      return;
    this.stage.sprites.stream().filter(s -> s != this).forEach(s -> s.whenIReceive(message));
    this.stage.whenIReceive(message);
  }

  /**
   * Broadcasts a message to all sprites in the stage except the current sprite,
   * and also to the
   * stage itself.
   *
   * @param message The message to be broadcasted. It can be any object.
   */
  public void broadcast(Object message) {
    if (stage == null)
      return;
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
   */
  public void whenIReceive(String message) {
  }

  /**
   * This method is called when a message is received. Override this method to
   * define custom
   * behavior.
   *
   * @see Sprite#broadcast(String)
   * @param message The message that is received.
   */
  public void whenIReceive(Object message) {
  }

  /**
   * @see stampToBackground
   */
  public void stamp() {
    this.stampToBackground();
  }

  /**
   * Stamps the current sprite to the background. A stamp is a non interactive
   * version of the
   * sprite.
   */
  public void stampToBackground() {
    if (this.costumes.size() > 0) {
      this.stage.backgroundStamps.add(this.getStamp());
    }
  }

  /**
   * Stamps the current sprite to the ui. A stamp is a non interactive version of
   * the sprite.
   */
  public void stampToUI() {
    if (this.costumes.size() > 0) {
      this.stage.uiStamps.add(this.getStamp());
    }
  }

  /**
   * Stamps the current sprite to the foreground. A stamp is a non interactive
   * version of the
   * sprite.
   */
  public void stampToForeground() {
    if (this.costumes.size() > 0) {
      this.stage.foregroundStamps.add(this.getStamp());
    }
  }

  /**
   * Sets the UI status of the sprite.
   *
   * @param isUI A boolean value indicating whether the sprite is part of the UI.
   */
  public void isUI(boolean isUI) {
    this.isUI = isUI;
  }

  /**
   * Checks if the sprite is part of the user interface.
   *
   * @return true if the sprite is part of the user interface, false otherwise.
   */
  public boolean isUI() {
    return this.isUI;
  }

  /**
   * This method is intended to be overridden by subclasses to define the behavior
   * of the sprite
   * when it is run. By default, this method does nothing.
   *
   * <p>
   * It is called every frame.
   */
  public void run() {
  }

  protected void addedToStage(Stage stage) {
    this.stage = stage;
    this.pen.addedToStage(stage);
    this.text.addedToStage(stage);
    this.whenAddedToStage();
    this.whenAddedToStage(stage);
  }

  protected void removedFromStage(Stage stage) {
    this.pen.removedFromStage(stage);
    this.text.removedFromStage(stage);
    this.stage = null;
    this.whenRemovedFromStage();
    this.whenRemovedFromStage(stage);
  }

  /** Draws the sprite if it is not hidden. */
  protected void draw(PGraphics buffer) {
    if (this.stage == null)
      return;
    if (this.costumes.size() > 0 && this.show) {
      var shader = this.getCurrentShader();
      this.costumes
          .get(this.currentCostume)
          .draw(buffer, this.size, this.direction, this.x, this.y, this.rotationStyle, shader);
    }
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
}
