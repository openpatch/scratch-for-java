package org.openpatch.scratch;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openpatch.scratch.extensions.hitbox.Hitbox;
import org.openpatch.scratch.extensions.math.Vector2;
import org.openpatch.scratch.extensions.pen.Pen;
import org.openpatch.scratch.extensions.text.Text;
import org.openpatch.scratch.extensions.text.TextStyle;
import org.openpatch.scratch.extensions.timer.Timer;
import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.Color;
import org.openpatch.scratch.internal.Drawable;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Sound;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Sprite implements Drawable {

  private CopyOnWriteArrayList<Image> costumes = new CopyOnWriteArrayList<>();
  private int currentCostume = 0;
  private CopyOnWriteArrayList<Sound> sounds = new CopyOnWriteArrayList<>();
  private boolean show = true;
  private float size = 100;
  private boolean onEdgeBounce = false;
  private RotationStyle rotationStyle = RotationStyle.ALL_AROUND;
  private float x = 0;
  private float y = 0;
  private float direction = 0;
  private Stage stage;
  private final ConcurrentHashMap<String, Timer> timer;
  private final Pen pen;
  private Hitbox hitbox;
  private final Text text;

  public Sprite() {
    this.pen = new Pen();
    this.timer = new ConcurrentHashMap<>();
    this.text = new Text(this);
    this.x = Applet.getInstance().getWidth() / 2.0f;
    this.y = Applet.getInstance().getHeight() / 2.0f;
    this.timer.put("default", new Timer());
  }

  public Sprite(final String name, final String imagePath) {
    this();
    final Image costume = new Image(name, imagePath);
    this.costumes.add(costume);
  }

  /**
   * Copies a Sprite object.
   *
   * @param s a Sprite object to copy
   */
  public Sprite(final Sprite s) {
    this.costumes = new CopyOnWriteArrayList<>();
    for (final Image costume : s.costumes) {
      this.costumes.add(new Image(costume));
    }
    this.currentCostume = s.currentCostume;
    this.sounds = new CopyOnWriteArrayList<>();
    for (final Sound sound : s.sounds) {
      this.sounds.add(new Sound(sound));
    }
    this.show = s.show;
    this.size = s.size;
    this.onEdgeBounce = s.onEdgeBounce;
    this.direction = s.direction;
    this.x = s.x;
    this.y = s.y;
    this.timer = new ConcurrentHashMap<>();
    this.pen = new Pen(s.pen);
    this.text = new Text(s.text);
    this.stage = s.stage;
  }

  public void addedToStage(final Stage stage) {
    this.stage = stage;
    this.pen.addedToStage(stage);
    this.text.addedToStage(stage);
    Applet.getInstance().registerMethod("keyEvent", this);
    Applet.getInstance().registerMethod("mouseEvent", this);
  }

  public void removedFromStage(final Stage stage) {
    this.pen.removedFromStage(stage);
    Applet.getInstance().unregisterMethod("keyEvent", this);
    Applet.getInstance().unregisterMethod("mouseEvent", this);
    this.stage = null;
  }

  public void remove() {
    if (this.stage != null) {
      this.stage.remove(this);
    }
  }

  public Stage getStage() {
    return this.stage;
  }

  /**
   * Add a costume to the sprite. If a costume with the received name already
   * exists do nothing.
   *
   * @param name      a unique name
   * @param imagePath a image path
   */
  public void addCostume(final String name, final String imagePath) {
    for (final Image costume : this.costumes) {
      if (costume.getName().equals(name)) {
        return;
      }
    }

    final Image costume = new Image(name, imagePath);
    this.costumes.add(costume);
  }

  public void addCostume(final String name, final String spriteSheetPath, final int x, final int y, final int width, final int height) {
    for (final Image costume : this.costumes) {
      if (costume.getName().equals(name)) {
        return;
      }
    }

    final Image costume = new Image(name, spriteSheetPath, x, y, width, height);
    this.costumes.add(costume);
  }

  /**
   * Switch to a costume by name.
   *
   * @param name the name of a costume
   */
  public void switchCostume(final String name) {
    for (int i = 0; i < this.costumes.size(); i++) {
      final Image costume = this.costumes.get(i);
      if (costume.getName().equals(name)) {
        this.currentCostume = i;
        return;
      }
    }
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
  public void addSound(final String name, final String soundPath) {
    for (final Sound sound : this.sounds) {
      if (sound.getName().equals(name)) {
        return;
      }
    }

    final Sound sound = new Sound(name, soundPath);
    this.sounds.add(sound);
  }

  /**
   * Remove a sound from the sprite.
   *
   * @param name the sound name
   */
  public void removeSound(final String name) {
    for (int i = 0; i < this.sounds.size(); i++) {
      final Sound sound = this.sounds.get(i);
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
  public void playSound(final String name) {
    for (final Sound sound : this.sounds) {
      if (sound.getName().equals(name) && !sound.isPlaying()) {
        sound.play();
      }
    }
  }

  /** Stops the playing of all sounds of the sprite. */
  public void stopAllSounds() {
    for (final Sound sound : this.sounds) {
      sound.stop();
    }
  }

  /**
   * Stops the playing of the sound with the given name
   *
   * @param name Name of the sound
   */
  public void stopSound(final String name) {
    for (final Sound sound : this.sounds) {
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
  public boolean isSoundPlaying(final String name) {
    for (final Sound sound : this.sounds) {
      if (sound.getName().equals(name)) {
        return sound.isPlaying();
      }
    }
    return false;
  }

  public void setTint(final int r, final int g, final int b) {
    this.setTint((float) b, (float) g, (float) b);
  }

  public void setTint(final Color c) {
    this.setTint(c.getRed(), c.getGreen(), c.getBlue());
  }

  /**
   * Sets the tint for the sprite with rgb.
   *
   * @see Image#setTint(float, float, float)
   */
  public void setTint(final float r, final float g, final float b) {
    if (this.costumes.size() == 0)
      return;

    for (final Image costume : this.costumes) {
      costume.setTint(r, g, b);
    }
  }

  /**
   * Sets the tint for the sprite with a hue.
   *
   * @see Image#setTint(float)
   */
  public void setTint(final float h) {
    if (this.costumes.size() == 0)
      return;

    for (final Image costume : this.costumes) {
      costume.setTint(h);
    }
  }

  /**
   * Changes the tint for the sprite.
   *
   * @see Image#changeTint(float)
   */
  public void changeTint(final float step) {
    if (this.costumes.size() == 0)
      return;

    for (final Image costume : this.costumes) {
      costume.changeTint(step);
    }
  }

  public void changeTint(final double step) {
    this.changeTint((float) step);
  }

  /**
   * Sets the transparency of the sprite.
   *
   * @see Image#setTransparency(float)
   * @param transparency 0 full transparency, 255 no transparency
   */
  public void setTransparency(final float transparency) {
    if (this.costumes.size() == 0)
      return;

    for (final Image costume : this.costumes) {
      costume.setTransparency(transparency);
    }
  }

  /**
   * Changes the transparency for the sprite.
   *
   * @see Image#changeTransparency(float)
   */
  public void changeTransparency(final float step) {
    if (this.costumes.size() == 0)
      return;

    for (final Image costume : this.costumes) {
      costume.changeTransparency(step);
    }
  }

  public void changeTransparency(final double step) {
    this.changeTransparency((float) step);
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
  public float getSize() {
    return this.size;
  }

  /**
   * Sets the size of the sprite.
   *
   * @param percentage a percentage [0...100]
   */
  public void setSize(final float percentage) {
    this.size = percentage;
    for (final Image costume : this.costumes) {
      costume.setSize(percentage);
    }
  }

  public void setSize(final double percentage) {
    this.setSize((float) percentage);
  }

  /**
   * Changes the size of the sprite by a given percentage.
   *
   * @param amount a percentage [0...100]
   */
  public void changeSize(final float amount) {
    this.size += amount;
  }

  public void changeSize(final double amount) {
    this.changeSize((float) amount);
  }

  /**
   * Sets if the sprite should bounce when hitting the edge of the screen. This
   * method is for making
   * is attribute perment.
   *
   * @param b
   */
  public void setOnEdgeBounce(final boolean b) {
    this.onEdgeBounce = b;
  }

  public void ifOnEdgeBounce() {
    final float newX = this.x;
    final float newY = this.y;
    Image currentCostume = null;
    if (this.costumes.size() > 0) {
      currentCostume = this.costumes.get(this.currentCostume);
    }
    final float costumeWidth = currentCostume != null ? currentCostume.getWidth() : this.pen.getSize();
    final float costumeHeight = currentCostume != null ? currentCostume.getHeight() : this.pen.getSize();

    final float spriteWidth = this.show ? costumeWidth : this.pen.getSize();
    if (newX > Applet.getInstance().getWidth() - spriteWidth / 2 || newX < spriteWidth / 2) {
      this.setDirection(this.calculateAngleOfReflection(this.direction, false));
    }

    final float spriteHeight = this.show ? costumeHeight : this.pen.getSize();
    if (newY > Applet.getInstance().getHeight() - spriteHeight / 2 || newY < spriteHeight / 2) {
      this.setDirection(this.calculateAngleOfReflection(this.direction, true));
    }
    this.setPosition(newX, newY);
  }

  public void setRotationStyle(final RotationStyle style) {
    this.rotationStyle = style;
  }

  /**
   * Sets the position of the sprite
   *
   * @param x a x coordinate
   * @param y a y coordinate
   */
  public void setPosition(final int x, final int y) {
    this.x = x;
    this.y = y;
    this.getPen().setPosition(x, y);
  }

  public void setPosition(final float x, final float y) {
    this.setPosition(Math.round(x), Math.round(y));
  }

  public void setPosition(final double x, final double y) {
    this.setPosition((float) x, (float) y);
  }

  /**
   * Sets the position of the sprite based on the coordinates of a given vector.
   *
   * @param v a vector
   */
  public void setPosition(final Vector2 v) {
    this.setPosition(v.getX(), v.getY());
  }

  /**
   * Rotates the sprite by a certain degrees to the left.
   *
   * @param degrees between 0 and 360
   */
  public void turnLeft(final float degrees) {
    this.setDirection(this.direction - degrees);
  }

  /**
   * Rotates the sprite by a certain degrees to the right.
   *
   * @param degrees between 0 and 360
   */
  public void turnRight(final float degrees) {
    this.setDirection(this.direction + degrees);
  }

  /**
   * Sets the direction of the sprite to a given degrees. When this value is 0 the
   * sprite move
   * right, when it is 180 is moves to the left.
   *
   * @param degrees between 0 and 360
   */
  public void setDirection(final float degrees) {
    this.direction = degrees;
    if (this.direction < 0) {
      this.direction += 360;
    }
    this.direction %= 360;
  }

  public void setDirection(final double degrees) {
    this.setDirection((float) degrees);
  }

  /**
   * Sets the direction of the sprite to the direction of a given vector.
   *
   * @param v a vector
   */
  public void setDirection(final Vector2 v) {
    this.setDirection(v.angle());
  }

  public void pointInDirection(final float degrees) {
    this.setDirection(degrees);
  }

  public void pointInDirection(final double degrees) {
    this.setDirection(degrees);
  }

  public void pointInDirection(final Vector2 v) {
    this.setDirection(v);
  }

  public void pointTowardsMousePointer() {
    final float mx = this.getMouseX();
    final float my = this.getMouseY();

    final double angle = new Vector2(mx - this.x, my - this.y).angle();
    this.setDirection(angle);
  }

  public void pointTowardsSprite(final Sprite s) {
    final float mx = s.getX();
    final float my = s.getY();

    final double angle = new Vector2(mx - this.x, my - this.y).angle();
    this.setDirection(angle);
  }

  /**
   * Returns the direction of the sprite.
   *
   * @return the direction [0...360]
   */
  public float getDirection() {
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
  public void move(final float steps) {
    // convert degrees to radians
    final float newX = steps * (float) Math.cos(this.direction * Math.PI / 180) + this.x;
    final float newY = steps * (float) Math.sin(this.direction * Math.PI / 180) + this.y;

    this.x = newX;
    this.y = newY;

    if (this.onEdgeBounce) {
      this.ifOnEdgeBounce();
    }

    this.pen.setPosition(this.x, this.y);
  }

  public void move(final double steps) {
    this.move((float) steps);
  }

  /**
   * Moves the sprite in the direction of the given vector. The length of the
   * vector determines how
   * move the sprite will move in this direction.
   *
   * @param v a vector
   */
  public void move(final Vector2 v) {
    this.setDirection(v.angle());
    this.move(v.length());
  }

  /**
   * Returns the x coordinate of the sprite
   *
   * @return a x coordinate
   */
  public float getX() {
    return this.x;
  }

  /**
   * Sets the x coordinate
   *
   * @param x a x coordinate
   */
  public void setX(final float x) {
    this.x = x;
    this.pen.setPosition(this.x, this.y);
  }

  public void setX(final double x) {
    this.setX((float) x);
  }

  /**
   * Changes x by a certain amount
   *
   * @param x number in pixels
   */
  public void changeX(final float x) {
    this.x += x;
    this.pen.setPosition(this.x, this.y);
  }

  public void changeX(final double x) {
    this.changeX((float) x);
  }

  /**
   * Returns the y coordinate of the sprite
   *
   * @return a y coordinate
   */
  public float getY() {
    return this.y;
  }

  /**
   * Sets the y coordinate
   *
   * @param y a y coordinate
   */
  public void setY(final float y) {
    this.y = y;
    this.pen.setPosition(this.x, this.y);
  }

  public void setY(final double y) {
    this.setY((float) y);
  }

  /**
   * Changes y by a certain amount
   *
   * @param y number in pixels
   */
  public void changeY(final float y) {
    this.y += y;
    this.pen.setPosition(this.x, this.y);
  }

  public void changeY(final double y) {
    this.changeY((float) y);
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
  public Timer getTimer(final String name) {
    return this.timer.get(name);
  }

  /**
   * Add a new timer by name. Overwriting default is not permitted.
   *
   * @param name the name of the timer
   */
  public void addTimer(final String name) {
    if ("default".equals(name))
      return;

    this.timer.put(name, new Timer());
  }

  /**
   * Remove a timer by name. Removing of default is not permitted.
   *
   * @param name the name of the timer
   */
  public void removeTimer(final String name) {
    if ("default".equals(name))
      return;

    this.timer.remove(name);
  }

  private float calculateAngleOfReflection(final float angleOfIncidence, final boolean horizontalWall) {
    if (horizontalWall) {
      float angleOfReflection = 360 - angleOfIncidence;
      while (angleOfReflection < 0)
        angleOfReflection += 360;
      return angleOfReflection;
    } else {
      float angleOfReflection = 180 - angleOfIncidence;
      while (angleOfReflection < 0)
        angleOfReflection += 360;
      return angleOfReflection;
    }
  }

  /**
   * Returns true is the mouse pointer is touching a non transparent area of the
   * sprite.
   *
   * @return true if touching
   */
  public boolean isTouchingMousePointer() {
    final float topLeftCornerX = this.x - this.getWidth() / 2.0f;
    final float topLeftCornerY = this.y - this.getHeight() / 2.0f;

    final float bottomRightCornerX = this.x + this.getWidth() / 2.0f;
    final float bottomRightCornerY = this.y + this.getHeight() / 2.0f;

    final float[] mouse = Stage.rotateXY(this.getMouseX(), this.getMouseY(), this.x, this.y, -this.direction);

    final boolean touching = mouse[0] > topLeftCornerX
        && mouse[1] > topLeftCornerY
        && mouse[0] < bottomRightCornerX
        && mouse[1] < bottomRightCornerY;

    if (touching) {
      final int relativeMouseX = Math.round(mouse[0] - topLeftCornerX);
      final int relativeMouseY = Math.round(mouse[1] - topLeftCornerY);

      if (this.costumes.size() > this.getCurrentCostumeIndex()) {
        final int color = this.costumes
            .get(this.getCurrentCostumeIndex())
            .getPixel(relativeMouseX, relativeMouseY);
        return Applet.getInstance().alpha(color) != 0;
      }
    }

    return false;
  }

  /**
   * Returns true if the rectangle which contains the image is outside of the
   * stage
   *
   * @return true if outside
   */
  public boolean isTouchingEdge() {
    Image currentCostume = null;
    if (this.costumes.size() > this.getCurrentCostumeIndex()) {
      currentCostume = this.costumes.get(this.getCurrentCostumeIndex());
    }
    final PApplet parent = Applet.getInstance();
    final float costumeWidth = currentCostume != null ? currentCostume.getWidth() : this.pen.getSize();
    final float costumeHeight = currentCostume != null ? currentCostume.getHeight() : this.pen.getSize();
    final float spriteWidth = this.show ? costumeWidth : this.pen.getSize();
    final float spriteHeight = this.show ? costumeHeight : this.pen.getSize();

    final float[] cornerTopLeft = Stage.rotateXY(this.x - spriteWidth / 2.0f, this.y - spriteHeight / 2.0f, this.x, this.y, this.direction);
    final float[] cornerTopRight = Stage.rotateXY(this.x + spriteWidth / 2.0f, this.y - spriteHeight / 2.0f, this.x, this.y, this.direction);
    final float[] cornerBottomLeft = Stage.rotateXY(this.x - spriteWidth / 2.0f, this.y + spriteHeight / 2.0f, this.x, this.y, this.direction);
    final float[] cornerBottomRight = Stage.rotateXY(this.x + spriteWidth / 2.0f, this.y + spriteHeight / 2.0f, this.x, this.y, this.direction);

    final float[][] corners = {
        cornerTopLeft, cornerTopRight, cornerBottomLeft, cornerBottomRight,
    };

    for (final float[] corner : corners) {
      final float cornerX = corner[0];
      final float cornerY = corner[1];
      if (cornerX > parent.width || cornerX < 0) {
        return true;
      }

      if (cornerY > parent.height || cornerY < 0) {
        return true;
      }
    }

    return false;
  }

  public float distanceToMousePointer() {
    final float x2 = this.getMouseX();
    final float y2 = this.getMouseY();
    final float x1 = this.getX();
    final float y1 = this.getY();
    return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  public float distanceToSprite(final Sprite sprite) {
    final float x2 = sprite.getX();
    final float y2 = sprite.getY();
    final float x1 = this.getX();
    final float y1 = this.getY();
    return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }

  public void setHitbox(final int... points) {
    final int l = points.length / 2;
    final int[] xPoints = new int[l];
    final int[] yPoints = new int[l];
    for (int i = 0; i < points.length; i += 2) {
      xPoints[i / 2] = points[i];
      yPoints[i / 2] = points[i + 1];
    }
    this.hitbox = new Hitbox(xPoints, yPoints);
  }

  public void setHitbox(final int[] xPoints, final int[] yPoints) {
    this.hitbox = new Hitbox(xPoints, yPoints);
  }

  public void setHitbox(final Hitbox hitbox) {
    this.hitbox = hitbox;
  }

  public Hitbox getHitbox() {
    Image currentCostume = null;
    if (this.costumes.size() > this.getCurrentCostumeIndex()) {
      currentCostume = this.costumes.get(this.getCurrentCostumeIndex());
    }
    final float costumeWidth = currentCostume != null ? currentCostume.getWidth() : this.pen.getSize();
    final float costumeHeight = currentCostume != null ? currentCostume.getHeight() : this.pen.getSize();
    final float spriteWidth = this.show ? costumeWidth : this.pen.getSize();
    final float spriteHeight = this.show ? costumeHeight : this.pen.getSize();

    if (this.hitbox != null) {
      this.hitbox.translateAndRotateAndResize(
          this.direction, this.x, this.y, this.x - spriteWidth / 2.0f, this.y - spriteHeight / 2.0f, this.size);
      return this.hitbox;
    }

    final float[] cornerTopLeft = Stage.rotateXY(this.x - spriteWidth / 2.0f, this.y - spriteHeight / 2.0f, this.x, this.y, this.direction);
    final float[] cornerTopRight = Stage.rotateXY(this.x + spriteWidth / 2.0f, this.y - spriteHeight / 2.0f, this.x, this.y, this.direction);
    final float[] cornerBottomLeft = Stage.rotateXY(this.x - spriteWidth / 2.0f, this.y + spriteHeight / 2.0f, this.x, this.y, this.direction);
    final float[] cornerBottomRight = Stage.rotateXY(this.x + spriteWidth / 2.0f, this.y + spriteHeight / 2.0f, this.x, this.y, this.direction);

    final int[] xPoints = new int[4];
    final int[] yPoints = new int[4];
    xPoints[0] = Math.round(cornerTopLeft[0]);
    yPoints[0] = Math.round(cornerTopLeft[1]);
    xPoints[1] = Math.round(cornerTopRight[0]);
    yPoints[1] = Math.round(cornerTopRight[1]);
    xPoints[2] = Math.round(cornerBottomRight[0]);
    yPoints[2] = Math.round(cornerBottomRight[1]);
    xPoints[3] = Math.round(cornerBottomLeft[0]);
    yPoints[3] = Math.round(cornerBottomLeft[1]);

    final Hitbox hitbox = new Hitbox(xPoints, yPoints);
    return hitbox;
  }

  public boolean isTouchingSprite(final Sprite sprite) {
    if (sprite == null || !sprite.show)
      return false;
    return this.getHitbox().intersects(sprite.getHitbox());
  }

  public boolean isTouchingSprite(final Class<? extends Sprite> c) {
    for (final Drawable d : this.stage.drawables) {
      if (c.isInstance(d) && this.isTouchingSprite((Sprite) d)) {
        return true;
      }
    }
    return false;
  }

  public Sprite getTouchingSprite(final Class<? extends Sprite> c) {
    for (final Drawable d : this.stage.drawables) {
      if (c.isInstance(d) && this.isTouchingSprite((Sprite) d)) {
        return (Sprite) d;
      }
    }
    return null;
  }

  public ArrayList<Sprite> getTouchingSprites(final Class<? extends Sprite> c) {
    final ArrayList<Sprite> sprites = new ArrayList<>();
    for (final Drawable d : this.stage.drawables) {
      if (c.isInstance(d) && this.isTouchingSprite((Sprite) d)) {
        sprites.add((Sprite) d);
      }
    }
    return sprites;
  }

  /**
   * Returns the current x-position of the mouse cursor
   *
   * @return x-position
   */
  public float getMouseX() {
    return this.stage.getMouseX();
  }

  /**
   * Returns the current y-position of the mouse cursor
   *
   * @return y-position
   */
  public float getMouseY() {
    return this.stage.getMouseY();
  }

  /**
   * Returns true is the mouse button is down
   *
   * @return mouse button down
   */
  public boolean isMouseDown() {
    return this.stage.isMouseDown();
  }

  /**
   * Returns true if the key is pressed
   *
   * @param keyCode a key code
   * @return key pressed
   */
  public boolean isKeyPressed(final int keyCode) {
    return this.stage.isKeyPressed(keyCode);
  }

  /**
   * Returns the current year
   *
   * @return current year
   */
  public int getCurrentYear() {
    return this.stage.getCurrentYear();
  }

  /**
   * Returns the current month
   *
   * @return current month
   */
  public int getCurrentMonth() {
    return this.stage.getCurrentMonth();
  }

  /**
   * Returns the current day of the month
   *
   * @return current day of the month
   */
  public int getCurrentDay() {
    return this.stage.getCurrentDay();
  }

  /**
   * Returns the current day of the week
   *
   * @return current day of the week
   */
  public int getCurrentDayOfWeek() {
    return this.stage.getCurrentDayOfWeek();
  }

  /**
   * Returns the current hour
   *
   * @return current hour
   */
  public int getCurrentHour() {
    return this.stage.getCurrentHour();
  }

  /**
   * Returns the current minute
   *
   * @return current minute
   */
  public int getCurrentMinute() {
    return this.stage.getCurrentMinute();
  }

  /**
   * Returns the current second
   *
   * @return current second
   */
  public int getCurrentSecond() {
    return this.stage.getCurrentSecond();
  }

  /**
   * Returns the current millisecond
   *
   * @return current millisecond
   */
  public int getCurrentMillisecond() {
    return this.stage.getCurrentMillisecond();
  }

  /**
   * Returns the days since 2010/01/01
   *
   * @return days since 2010/01/01
   */
  public int getDaysSince2000() {
    return this.stage.getDaysSince2000();
  }

  public void keyEvent(final KeyEvent e) {
    switch (e.getAction()) {
      case KeyEvent.PRESS:
        this.whenKeyPressed(e.getKeyCode());
        break;
      case KeyEvent.RELEASE:
        this.whenKeyReleased(e.getKeyCode());
        break;
    }
  }

  public void whenKeyPressed(final int keyCode) {
  }

  public void whenKeyReleased(final int keyCode) {
  }

  public void mouseEvent(final MouseEvent e) {
    this.whenMouseMoved(e.getX(), e.getY());
  }

  public void whenMouseMoved(final float x, final float y) {
  }

  public void whenClicked() {
  }

  public void goToFrontLayer() {
    this.stage.goToFrontLayer(this);
  }

  public void goToBackLayer() {
    this.stage.goToBackLayer(this);
  }

  public void goLayersForwards(final int number) {
    this.stage.goLayersForwards(this, number);
  }

  public void goLayersBackwards(final int number) {
    this.stage.goLayersBackwards(this, number);
  }

  public void whenBackdropSwitches(final String name) {
  }

  public int pickRandom(final int from, final int to) {
    if (to < from) {
      return to + (int) (Math.random() * (from - to));
    }
    return from + (int) (Math.random() * (to - from));
  }

  public Text getText() {
    return this.text;
  }

  public void think(final String text) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text);
  }

  public void think(final String text, final int millis) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text, millis);
  }

  public void say(final String text) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text);
  }

  public void say(final String text, final int millis) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text, millis);
  }

  /** Draws the sprite if it is not hidden. */
  public void draw() {
    if (this.stage == null)
      return;
    this.pen.draw();
    if (this.costumes.size() > 0 && this.show) {
      this.costumes
          .get(this.currentCostume)
          .draw(this.size, this.direction, this.x, this.y, this.rotationStyle);
    }

    if (Applet.getInstance().isDebug()) {
      this.getHitbox().draw();
    }
    this.text.setPosition(this.x + this.getWidth() * 0.9 / 2, this.y - this.getHeight() * 1.1 / 2);
    this.text.draw();
    this.run();
  }

  public void run() {
  }
}
