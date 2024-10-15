package org.openpatch.scratch;

import java.awt.Shape;
import java.util.AbstractMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.openpatch.scratch.extensions.hitbox.Hitbox;
import org.openpatch.scratch.extensions.math.Random;
import org.openpatch.scratch.extensions.math.Utils;
import org.openpatch.scratch.extensions.math.Vector2;
import org.openpatch.scratch.extensions.pen.Pen;
import org.openpatch.scratch.extensions.text.Text;
import org.openpatch.scratch.extensions.text.TextStyle;
import org.openpatch.scratch.extensions.timer.Timer;
import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.Color;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Sound;
import org.openpatch.scratch.internal.Stamp;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Sprite {

  private List<Image> costumes = new CopyOnWriteArrayList<>();
  private int currentCostume = 0;
  private List<Sound> sounds = new CopyOnWriteArrayList<>();
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

  public void addedToStage(Stage stage) {
    this.stage = stage;
    this.pen.addedToStage(stage);
    this.text.addedToStage(stage);
    this.whenAddedToStage();
    this.whenAddedToStage(stage);
  }

  public void removedFromStage(Stage stage) {
    this.pen.removedFromStage(stage);
    this.text.removedFromStage(stage);
    this.stage = null;
    this.whenRemovedFromStage();
    this.whenRemovedFromStage(stage);
  }

  public void whenAddedToStage() {}

  public void whenAddedToStage(Stage stage) {}

  public void whenRemovedFromStage() {}

  public void whenRemovedFromStage(Stage stage) {}

  public void remove() {
    if (this.stage != null) {
      this.stage.remove(this);
    }
  }

  public Stage getStage() {
    return this.stage;
  }

  /**
   * Add a costume to the sprite. If a costume with the received name already exists do nothing.
   *
   * @param name a unique name
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
   * Adds all tiles from a spritesheet as costumes. The costumes will be name by the prefix and the
   * index in the spritesheet.
   *
   * @param prefix a prefix for all generated costumes
   * @param spriteSheet a path to a sprite sheet
   * @param tileWidth the width of a single tile
   * @param tileHeight the height of a single tile
   */
  public void addCostumes(String prefix, String spriteSheet, int tileWidth, int tileHeight) {
    var image = Image.loadImage(spriteSheet);

    var nx = image.width / tileWidth;
    var ny = image.height / tileHeight;

    for (var y = 0; y < ny; y += 1) {
      for (var x = 0; x < nx; x += 1) {
        var index = x * nx + y;
        Image costume =
            new Image(
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

  public void switchCostume(double index) {
    this.currentCostume = (int) index % this.costumes.size();
  }

  /** Switch to the next costume. */
  public void nextCostume() {
    this.currentCostume = (this.currentCostume + 1) % this.costumes.size();
  }

  /**
   * Returns the current costume name
   *
   * @return a costume name
   */
  public String getCurrentCostumeName() {
    if (this.costumes.size() == 0) return null;

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
   * Add a sound to the sprite. If a sound with the received name already exists do nothing.
   *
   * @param name a unique name
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
    if (this.costumes.size() == 0) return;

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
    if (this.costumes.size() == 0) return;

    for (Image costume : this.costumes) {
      costume.setTint(h);
    }
  }

  /**
   * Changes the tint for the sprite.
   *
   * @see Image#changeTint(double)
   * @param h a hue value [0...255]
   */
  public void changeTint(double step) {
    if (this.costumes.size() == 0) return;

    for (Image costume : this.costumes) {
      costume.changeTint(step);
    }
  }

  public double getTint() {
    if (this.costumes.size() == 0) return 0;
    return this.costumes.get(currentCostume).getTint();
  }

  /**
   * Sets the transparency of the sprite.
   *
   * @see Image#setTransparency(double)
   * @param transparency 0 full transparency, 255 no transparency
   */
  public void setTransparency(double transparency) {
    if (this.costumes.size() == 0) return;

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
    if (this.costumes.size() == 0) return;

    for (Image costume : this.costumes) {
      costume.changeTransparency(step);
    }
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
   * Sets if the sprite should bounce when hitting the edge of the screen. This method is for making
   * is attribute perment.
   *
   * @param b true if the sprite should bounce
   */
  public void setOnEdgeBounce(boolean b) {
    this.onEdgeBounce = b;
  }

  public void ifOnEdgeBounce() {
    if (this.hitboxDisabled || this.isUI) return;

    var h = this.getHitbox();

    if (h.intersects(this.stage.leftBorder)) {
      this.setDirection(-this.getDirection());
    } else if (h.intersects(this.stage.rightBorder)) {
      this.setDirection(-this.getDirection());
    } else if (h.intersects(this.stage.topBorder)) {
      this.setDirection(-this.getDirection() - 180);
    } else if (h.intersects(this.stage.bottomBorder)) {
      this.setDirection(-this.getDirection() - 180);
    }
  }

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
   * Sets the direction of the sprite to a given degrees. When this value is 0 the sprite move
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

  public void pointInDirection(double degrees) {
    this.setDirection(degrees);
  }

  public void pointInDirection(Vector2 v) {
    double angle = v.sub(this.getPosition()).angle();
    this.setDirection(90 - angle);
  }

  public void pointTowardsMousePointer() {
    var mx = this.getMouseX();
    var my = this.getMouseY();

    double angle = new Vector2(mx - this.x, my - this.y).angle();
    this.setDirection(90 - angle);
  }

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
   * Moves the sprite in the direction of the given vector. The length of the vector determines how
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
   * Return the width of the current costume or the pen size, when no costume is available.
   *
   * @return the width of the sprite
   */
  public int getWidth() {
    if (this.costumes.size() == 0) return (int) this.getPen().getSize();

    return this.costumes.get(this.currentCostume).getWidth();
  }

  /**
   * Return the height of the current costume or the pen size, when no costume is available.
   *
   * @return the height of the sprite
   */
  public int getHeight() {
    if (this.costumes.size() == 0) return (int) this.getPen().getSize();

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
    if ("default".equals(name)) return;

    this.timer.put(name, new Timer());
  }

  /**
   * Remove a timer by name. Removing of default is not permitted.
   *
   * @param name the name of the timer
   */
  public void removeTimer(String name) {
    if ("default".equals(name)) return;

    this.timer.remove(name);
  }

  /**
   * Returns true is the mouse pointer is touching a non transparent area of the sprite.
   *
   * @return true if touching
   */
  public boolean isTouchingMousePointer() {
    if (this.hitboxDisabled) return false;

    var mx = this.getMouseX();
    var my = this.getMouseY();

    if (isUI && this.stage != null) {
      mx = this.stage.getCamera().toGlobalX(mx);
      my = this.stage.getCamera().toGlobalY(my);
    }

    double[] mouse = Utils.rotateXY(mx, my, this.x, this.y, this.direction - 90);

    var relativeMouseX = (int) Math.round(mouse[0] - this.x + this.getWidth() / 2);
    var relativeMouseY = (int) -Math.round(mouse[1] - this.y - this.getHeight() / 2);

    if (this.costumes.size() > this.getCurrentCostumeIndex()) {
      int color =
          this.costumes.get(this.getCurrentCostumeIndex()).getPixel(relativeMouseX, relativeMouseY);
      return Applet.getInstance().alpha(color) != 0;
    }

    return false;
  }

  /**
   * Returns true if the rectangle which contains the image is outside of the stage
   *
   * @return true if outside
   */
  public boolean isTouchingEdge() {
    if (this.hitboxDisabled) return false;
    var h = this.getHitbox();
    return h.intersects(this.stage.topBorder)
        || h.intersects(this.stage.bottomBorder)
        || h.intersects(this.stage.leftBorder)
        || h.intersects(this.stage.rightBorder);
  }

  public double distanceToMousePointer() {
    var x2 = this.getMouseX();
    var y2 = this.getMouseY();
    var x1 = this.getX();
    var y1 = this.getY();
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  public double distanceToSprite(Sprite sprite) {
    var x2 = sprite.getX();
    var y2 = sprite.getY();
    var x1 = this.getX();
    var y1 = this.getY();
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

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

  public void setHitbox(int[] xPoints, final int[] yPoints) {
    this.hitbox = new Hitbox(xPoints, yPoints);
  }

  public void setHitbox(Hitbox hitbox) {
    this.hitbox = hitbox;
  }

  public void setHitbox(Shape shape) {
    this.hitbox = new Hitbox(shape);
  }

  public void disableHitbox() {
    this.hitboxDisabled = true;
  }

  public void enableHitbox() {
    this.hitboxDisabled = false;
  }

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

    var cornerTopLeft =
        Utils.rotateXY(
            this.x - spriteWidth / 2.0f, -this.y - spriteHeight / 2.0f, this.x, -this.y, rotation);
    var cornerTopRight =
        Utils.rotateXY(
            this.x + spriteWidth / 2.0f, -this.y - spriteHeight / 2.0f, this.x, -this.y, rotation);
    var cornerBottomLeft =
        Utils.rotateXY(
            this.x - spriteWidth / 2.0f, -this.y + spriteHeight / 2.0f, this.x, -this.y, rotation);
    var cornerBottomRight =
        Utils.rotateXY(
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

  public boolean isTouchingSprite(Sprite sprite) {
    if (sprite == this) return false;
    if (stage == null) return false;
    if (sprite == null || !sprite.show || sprite.hitboxDisabled) return false;
    return this.getHitbox().intersects(sprite.getHitbox());
  }

  public boolean isTouchingSprite(Class<? extends Sprite> c) {
    if (stage == null) return false;
    return this.stage.sprites.stream()
        .filter(s -> !s.isUI())
        .filter(s -> c.isInstance(s) && this.isTouchingSprite(s))
        .findFirst()
        .isPresent();
  }

  public <T extends Sprite> T getTouchingSprite(Class<T> c) {
    if (stage == null) return null;
    return this.stage.sprites.stream()
        .filter(s -> !s.isUI())
        .filter(s -> c.isInstance(s) && this.isTouchingSprite(s))
        .findFirst()
        .map(c::cast)
        .orElse(null);
  }

  public <T extends Sprite> List<T> getTouchingSprites(Class<T> c) {
    if (stage == null) return null;
    return this.stage.sprites.stream()
        .filter(s -> !s.isUI())
        .filter(s -> c.isInstance(s) && this.isTouchingSprite(s))
        .map(c::cast)
        .collect(Collectors.toList());
  }

  /**
   * Returns the current x-position of the mouse cursor
   *
   * @return x-position
   */
  public double getMouseX() {
    if (stage == null) return 0;
    return this.stage.getMouseX();
  }

  /**
   * Returns the current y-position of the mouse cursor
   *
   * @return y-position
   */
  public double getMouseY() {
    if (this.stage == null) return 0;
    return this.stage.getMouseY();
  }

  public Vector2 getMouse() {
    return new Vector2(this.getMouseX(), this.getMouseY());
  }

  /**
   * Returns true is the mouse button is down
   *
   * @return mouse button down
   */
  public boolean isMouseDown() {
    if (this.stage == null) return false;
    return this.stage.isMouseDown();
  }

  /**
   * Returns true if the key is pressed
   *
   * @param keyCode a key code
   * @return key pressed
   */
  public boolean isKeyPressed(int keyCode) {
    if (this.stage == null) return false;
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
    if (stage == null) return 0;
    return this.stage.getCurrentYear();
  }

  /**
   * Returns the current month
   *
   * @return current month
   */
  public int getCurrentMonth() {
    if (stage == null) return 0;
    return this.stage.getCurrentMonth();
  }

  /**
   * Returns the current day of the month
   *
   * @return current day of the month
   */
  public int getCurrentDay() {
    if (stage == null) return 0;
    return this.stage.getCurrentDay();
  }

  /**
   * Returns the current day of the week
   *
   * @return current day of the week
   */
  public int getCurrentDayOfWeek() {
    if (stage == null) return 0;
    return this.stage.getCurrentDayOfWeek();
  }

  /**
   * Returns the current hour
   *
   * @return current hour
   */
  public int getCurrentHour() {
    if (stage == null) return 0;
    return this.stage.getCurrentHour();
  }

  /**
   * Returns the current minute
   *
   * @return current minute
   */
  public int getCurrentMinute() {
    if (stage == null) return 0;
    return this.stage.getCurrentMinute();
  }

  /**
   * Returns the current second
   *
   * @return current second
   */
  public int getCurrentSecond() {
    if (stage == null) return 0;
    return this.stage.getCurrentSecond();
  }

  /**
   * Returns the current millisecond
   *
   * @return current millisecond
   */
  public int getCurrentMillisecond() {
    if (stage == null) return 0;
    return this.stage.getCurrentMillisecond();
  }

  /**
   * Returns the days since 2010/01/01
   *
   * @return days since 2010/01/01
   */
  public int getDaysSince2000() {
    if (stage == null) return 0;
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

  public void whenKeyPressed(int keyCode) {}

  public void whenKeyReleased(int keyCode) {}

  public void mouseEvent(MouseEvent e) {}

  public void whenMouseMoved(double x, double y) {}

  public void whenMouseClicked(MouseCode mouseCode) {}

  public void whenClicked() {}

  public void goToRandomPosition() {
    this.setPosition(
        Random.randomInt(-this.stage.getWidth() / 2, this.stage.getWidth() / 2),
        Random.randomInt(-this.stage.getHeight() / 2, this.stage.getHeight() / 2));
  }

  public void goToMousePointer() {
    this.setPosition(this.getMouseX(), this.getMouseY());
  }

  public void goToSprite(Sprite sprite) {
    this.setPosition(sprite.getX(), sprite.getY());
  }

  public void goToFrontLayer() {
    if (stage == null) return;
    this.stage.goToFrontLayer(this);
  }

  public void goToBackLayer() {
    if (stage == null) return;
    this.stage.goToBackLayer(this);
  }

  public void goLayersForwards(int number) {
    if (stage == null) return;
    this.stage.goLayersForwards(this, number);
  }

  public void goLayersBackwards(int number) {
    if (stage == null) return;
    this.stage.goLayersBackwards(this, number);
  }

  public void whenBackdropSwitches(String name) {}

  public int pickRandom(int from, final int to) {
    if (to < from) {
      return to + (int) (Math.random() * (from - to));
    }
    return from + (int) (Math.random() * (to - from));
  }

  public Text getText() {
    return this.text;
  }

  public void think(String text) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text);
  }

  public void think(String text, final int millis) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text, millis);
  }

  public void say(String text) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text);
  }

  public void say(String text, final int millis) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text, millis);
  }

  public void broadcast(String message) {
    if (stage == null) return;
    this.stage.sprites.stream().filter(s -> s != this).forEach(s -> s.whenIReceive(message));
    this.stage.whenIReceive(message);
  }

  public void broadcast(Object message) {
    if (stage == null) return;
    this.stage.sprites.stream().filter(s -> s != this).forEach(s -> s.whenIReceive(message));
    this.stage.whenIReceive(message);
  }

  public void whenIReceive(String message) {}

  public void whenIReceive(Object message) {}

  /**
   * @see stampToBackground
   */
  public void stamp() {
    this.stampToBackground();
  }

  /**
   * Stamps the current sprite to the background. A stamp is a non interactive version of the
   * sprite.
   */
  public void stampToBackground() {
    if (this.costumes.size() > 0) {
      this.stage.backgroundStamps.add(this.getStamp());
    }
  }

  /** Stamps the current sprite to the ui. A stamp is a non interactive version of the sprite. */
  public void stampToUI() {
    if (this.costumes.size() > 0) {
      this.stage.uiStamps.add(this.getStamp());
    }
  }

  private Stamp getStamp() {
    var stamp =
        new Stamp(
            this.costumes.get(this.currentCostume),
            this.direction,
            this.x,
            this.y,
            this.rotationStyle);

    return stamp;
  }

  /**
   * Stamps the current sprite to the foreground. A stamp is a non interactive version of the
   * sprite.
   */
  public void stampToForeground() {
    if (this.costumes.size() > 0) {
      this.stage.foregroundStamps.add(this.getStamp());
    }
  }

  public void isUI(boolean isUI) {
    this.isUI = isUI;
  }

  public boolean isUI() {
    return this.isUI;
  }

  /** Draws the sprite if it is not hidden. */
  public void draw() {
    if (this.stage == null) return;
    if (this.costumes.size() > 0 && this.show) {
      this.costumes
          .get(this.currentCostume)
          .draw(this.size, this.direction, this.x, this.y, this.rotationStyle);
    }
  }

  public void drawDebug() {
    if (!this.hitboxDisabled && !this.isUI) {
      this.getHitbox().drawDebug(this.getStage().getDebugBuffer());
    }
    if (this.costumes.size() > 0 && this.show) {
      this.costumes
          .get(this.currentCostume)
          .drawDebug(
              this.getStage().getDebugBuffer(),
              this.size,
              this.direction,
              this.x,
              this.y,
              this.rotationStyle);
    }
  }

  /**
   * This method is called 60-times per second, if the sprite object was added to a stage object.
   */
  public void run() {}
}
