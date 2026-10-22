package org.openpatch.scratch;

import java.util.ArrayList;
import java.util.List;
import org.openpatch.scratch.internal.Costume;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.Monitor;
import org.openpatch.scratch.internal.SoundBank;
import org.openpatch.scratch.internal.compose.Compositor;
import org.openpatch.scratch.internal.draw.Effects;
import org.openpatch.scratch.internal.draw.SpriteDraw;
import org.openpatch.scratch.internal.geom.Geometry;
import org.openpatch.scratch.internal.image.Images;

/**
 * A sprite: an object on the stage with costumes, a position and a direction, and code that runs
 * every tick in {@link #run()}, like a sprite and its scripts in Scratch.
 *
 * <pre>{@code
 * public class Cat extends Sprite {
 *   public Cat() {
 *     this.addCostume("cat", "assets/cat.png");
 *   }
 *
 *   public void run() {
 *     this.move(2);
 *     this.ifOnEdgeBounce();
 *   }
 * }
 * }</pre>
 *
 * <p>Positions are Scratch's: (0, 0) is the centre of the stage, x grows to the right and y
 * upwards. Directions are Scratch's too: 0 points up, 90 to the right (the start direction), 180
 * down, 270 to the left. Everything a sprite does happens in ticks of 1/60 s of game time, so
 * {@code move(5)} in {@code run()} moves 5 steps per tick on every computer.
 */
public class Sprite implements Cloneable {

  /** Direction pointing up (0 degrees). */
  public static final double DIRECTION_UP = 0;
  /** Direction pointing right (90 degrees), where a new sprite points. */
  public static final double DIRECTION_RIGHT = 90;
  /** Direction pointing down (180 degrees). */
  public static final double DIRECTION_DOWN = 180;
  /** Direction pointing left (270 degrees). */
  public static final double DIRECTION_LEFT = 270;

  private Stage stage;
  private List<Costume> costumes = new ArrayList<>();
  private int currentCostume;
  private double[] rotationCenter;
  private double x, y;
  private double direction = 90;
  private double size = 100;
  private RotationStyle rotationStyle = RotationStyle.ALL_AROUND;
  private boolean visible = true;
  private Layer layer = Layer.MAIN;
  private double colorEffect;
  private double transparency;
  private Color tint;
  private Shape hitbox;
  // the last world hitbox and what it was made from (shapes cannot change, so it can be shared)
  private Shape worldHitbox;
  private Costume worldHitboxCostume;
  private Shape worldHitboxLocal;
  private double[] worldHitboxCentre;
  private RotationStyle worldHitboxStyle;
  private double[] worldHitboxKey;
  private boolean hitboxDisabled;
  private boolean isClone;
  private Pen pen;
  private SoundBank sounds = new SoundBank();
  private Timer timer = new Timer();
  private Text text;
  private java.util.Map<String, Monitor> monitors = new java.util.LinkedHashMap<>();
  private Shaders shaders = new Shaders();
  // animations: name -> costume indices; playing one advances it every interval of game time
  private java.util.Map<String, int[]> animations = new java.util.HashMap<>();
  private String animation;
  private int animationFrame;
  private boolean animationDone;
  private double animationInterval = 0.12;
  private double animationLastStep;
  // a size of its own instead of the costume's at `size` percent, optionally nine-sliced
  private double width, height;
  private int[] nineSlice;
  // glide: from, to, and how far along in ticks
  private double glideFromX, glideFromY, glideToX, glideToY;
  private int glideTicks, glideTick = -1;

  private org.openpatch.scratch.internal.ExtensionHost.Attachments attachments =
      new org.openpatch.scratch.internal.ExtensionHost.Attachments();

  /**
   * An extension's state for this sprite, made on first use; a clone gets its own. For
   * extensions, not for learner code.
   *
   * @param type the state's class
   * @param factory makes it
   * @param <T> the state
   * @return the sprite's one instance
   */
  public <T> T extension(Class<T> type, java.util.function.Supplier<T> factory) {
    return this.attachments.get(type, factory);
  }

  /** A sprite without costumes. Add some with {@link #addCostume(String, String)}. */
  public Sprite() {
    this.pen = new Pen(this);
    this.text = new Text(this);
  }

  /**
   * A sprite with one costume.
   *
   * @param name the costume's name
   * @param imagePath the image file
   */
  public Sprite(String name, String imagePath) {
    this();
    this.addCostume(name, imagePath);
  }

  // ---- stage -----------------------------------------------------------------

  /**
   * Called when the sprite was added to a stage. Override it to react.
   *
   * @example.files SpriteWhenAddedToStage.java
   */
  public void whenAddedToStage() {}

  /**
   * Called when the sprite was removed from its stage. Override it to react.
   *
   * @example.files SpriteWhenRemovedFromStage.java
   */
  public void whenRemovedFromStage() {}

  /**
   * @return the stage the sprite is on, or null
   *
   * @example.files SpriteGetStage.java
   */
  public Stage getStage() {
    return this.stage;
  }

  void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Removes the sprite from its stage.
   *
   * @example.files SpriteRemove.java
   */
  public void remove() {
    if (this.stage != null) {
      this.stage.remove(this);
    }
  }

  /**
   * Prints values for debugging, but only while debug mode is on ({@code window.setDebug(true)}
   * or F12).
   *
   * @param values what to print
   *
   * @example.files SpriteDebug.java
   */
  public void debug(Object... values) {
    Game game = Game.currentOrNull();
    if (game != null && game.isDebug()) {
      StringBuilder line = new StringBuilder(this.getClass().getSimpleName()).append(':');
      for (Object v : values) {
        line.append(' ').append(v);
      }
      System.out.println(line);
    }
  }

  /**
   * Shows a variable in a box at the top left of the stage, like a Scratch monitor; its value is
   * read every frame.
   *
   * <pre>{@code
   * this.showVariable("score", () -> this.score);
   * }</pre>
   *
   * @param name the label
   * @param value gives the value to show
   *
   * @scratchblock show variable [name v]
   * @example.files SpriteShowVariable.java
   */
  public void showVariable(String name, java.util.function.Supplier<?> value) {
    this.monitors.put(name, new Monitor(monitorOwner() + ": " + name, value));
  }

  /**
   * @param name the label of a variable to hide again
   *
   * @scratchblock hide variable [name v]
   * @example.files SpriteHideVariable.java
   */
  public void hideVariable(String name) {
    this.monitors.remove(name);
  }

  private String monitorOwner() {
    String owner = this.getClass().getSimpleName();
    return owner.isEmpty() ? "Sprite" : owner;
  }

  java.util.Collection<Monitor> monitors() {
    return this.monitors.values();
  }

  // ---- costumes --------------------------------------------------------------

  /**
   * Adds a costume from an image file. The first costume added is the one the sprite wears.
   *
   * @param name the costume's name, for {@link #switchCostume(String)}
   * @param imagePath the image file (PNG, JPEG, GIF, BMP or TGA)
   *
   * @example.files SpriteAddCostume.java
   */
  public void addCostume(String name, String imagePath) {
    this.costumes.add(new Costume(name, Images.load(imagePath)));
  }

  /**
   * Adds a costume from an image file, named after the file.
   *
   * @param imagePath the image file
   *
   * @example.files SpriteAddCostume.java
   */
  public void addCostume(String imagePath) {
    String file = imagePath.substring(Math.max(imagePath.lastIndexOf('/'), imagePath.lastIndexOf('\\')) + 1);
    int dot = file.lastIndexOf('.');
    this.addCostume(dot > 0 ? file.substring(0, dot) : file, imagePath);
  }

  /**
   * Adds a costume cut out of a larger image, e.g. one frame of a sprite sheet.
   *
   * @param name the costume's name
   * @param imagePath the image file
   * @param x left edge of the part, in pixels
   * @param y top edge of the part, in pixels
   * @param width width of the part
   * @param height height of the part
   *
   * @example.files SpriteAddCostume.java
   */
  public void addCostume(String name, String imagePath, int x, int y, int width, int height) {
    this.costumes.add(new Costume(name, Images.load(imagePath).region(x, y, width, height)));
  }

  /**
   * Adds every frame of a sprite sheet as a costume, row by row, named {@code name0},
   * {@code name1}, …
   *
   * @param name the start of the costumes' names
   * @param imagePath the image file
   * @param width width of one frame
   * @param height height of one frame
   *
   * @example.files SpriteAddCostumes.java
   */
  public void addCostumes(String name, String imagePath, int width, int height) {
    var sheet = Images.load(imagePath);
    int i = 0;
    for (int y = 0; y + height <= sheet.getHeight(); y += height) {
      for (int x = 0; x + width <= sheet.getWidth(); x += width) {
        this.costumes.add(new Costume(name + i++, sheet.region(x, y, width, height)));
      }
    }
  }

  /**
   * Switches to the costume with that name; an unknown name changes nothing.
   *
   * @param name the costume's name
   *
   * @scratchblock switch costume to [name v]
   * @example.files SpriteSwitchCostume.java
   */
  public void switchCostume(String name) {
    for (int i = 0; i < this.costumes.size(); i++) {
      if (this.costumes.get(i).name().equals(name)) {
        this.currentCostume = i;
        return;
      }
    }
  }

  /**
   * Switches to a costume by number, counting from 0; numbers wrap around like Scratch's.
   *
   * @param index the costume's number
   *
   * @scratchblock switch costume to [name v]
   * @example.files SpriteSwitchCostume.java
   */
  public void switchCostume(double index) {
    if (!this.costumes.isEmpty()) {
      this.currentCostume = Math.floorMod((int) Math.floor(index), this.costumes.size());
    }
  }

  /**
   * Switches to the next costume, after the last one to the first.
   *
   * @scratchblock next costume
   * @example.files SpriteNextCostume.java
   */
  public void nextCostume() {
    this.switchCostume(this.currentCostume + 1);
  }

  /**
   * Switches to the previous costume, before the first one to the last.
   *
   * @scratchblock switch costume to (previous costume)
   * @example.files SpritePreviousCostume.java
   */
  public void previousCostume() {
    this.switchCostume(this.currentCostume - 1);
  }

  /**
   * @return the current costume's name, or null without costumes
   *
   * @example.files SpriteGetCurrentCostumeName.java
   * @scratchblock (costume [name v])
   */
  public String getCurrentCostumeName() {
    return this.costumes.isEmpty() ? null : this.costumes.get(this.currentCostume).name();
  }

  /**
   * @return the current costume's number, counting from 0
   *
   * @example.files SpriteGetCurrentCostumeIndex.java
   * @scratchblock (costume [number v])
   */
  public int getCurrentCostumeIndex() {
    return this.currentCostume;
  }

  Costume costume() {
    return this.costumes.isEmpty() ? null : this.costumes.get(this.currentCostume);
  }

  // ---- animations -------------------------------------------------------------------

  /**
   * Adds an animation from numbered image files: the pattern's {@code %d} counts from 1.
   *
   * <pre>{@code
   * this.addAnimation("walk", "assets/walk%d.png", 4); // walk1.png … walk4.png
   * }</pre>
   *
   * @param name the animation's name
   * @param pattern the file names, with {@code %d} for the frame number
   * @param frames how many frames
   *
   * @example.folder AnimatedSpriteAddAnimation
   */
  public void addAnimation(String name, String pattern, int frames) {
    int[] numbers = new int[frames];
    for (int i = 0; i < frames; i++) {
      numbers[i] = i + 1;
    }
    this.addNumberedAnimation(name, pattern, numbers);
  }

  /**
   * Adds an animation from numbered image files, from one number to another: for files that count
   * from 0, or to use only some of them. If {@code to} is smaller than {@code from}, it counts down.
   *
   * <pre>{@code
   * this.addAnimation("hit", "assets/hit%d.png", 0, 4); // hit0.png … hit4.png
   * }</pre>
   *
   * @param name the animation's name
   * @param pattern the file names, with {@code %d} for the frame number
   * @param from the number of the first frame
   * @param to the number of the last frame
   *
   * @example.folder AnimatedSpriteAddAnimation
   */
  public void addAnimation(String name, String pattern, int from, int to) {
    int step = to >= from ? 1 : -1;
    int[] numbers = new int[Math.abs(to - from) + 1];
    for (int i = 0; i < numbers.length; i++) {
      numbers[i] = from + i * step;
    }
    this.addNumberedAnimation(name, pattern, numbers);
  }

  private void addNumberedAnimation(String name, String pattern, int[] numbers) {
    int[] indices = new int[numbers.length];
    for (int i = 0; i < numbers.length; i++) {
      indices[i] = this.costumes.size();
      this.costumes.add(new Costume(name + "#" + i, Images.load(String.format(pattern, numbers[i]))));
    }
    this.animations.put(name, indices);
  }

  /**
   * Adds an animation from a sprite sheet: frames of equal size, numbered row by row from the
   * top left, starting at 0.
   *
   * <pre>{@code
   * this.addAnimation("walk", "assets/walk.png", 32, 32, 0, 1, 2, 3);   // the first row
   * this.addAnimation("down", "assets/hero.png", 32, 32, 0, 4, 8, 12);  // the first column of 4
   * }</pre>
   *
   * @param name the animation's name
   * @param sheetPath the sprite sheet
   * @param width the width of a frame
   * @param height the height of a frame
   * @param frames the frames to show, in order
   *
   * @example.folder AnimatedSpriteAddAnimation
   */
  public void addAnimation(String name, String sheetPath, int width, int height, int... frames) {
    var sheet = Images.load(sheetPath);
    int columns = Math.max(1, sheet.getWidth() / width);
    int[] indices = new int[frames.length];
    for (int i = 0; i < frames.length; i++) {
      int column = frames[i] % columns, row = frames[i] / columns;
      indices[i] = this.costumes.size();
      this.costumes.add(new Costume(name + "#" + i, sheet.region(column * width, row * height, width, height)));
    }
    this.animations.put(name, indices);
  }

  /**
   * Plays an animation, over and over. Call it every tick (in {@code run()}): it shows the next
   * frame each time the animation interval has passed, and starts from the first frame when
   * another animation was playing before.
   *
   * @param name the animation's name
   *
   * @example.folder AnimatedSpritePlayAnimation
   */
  public void playAnimation(String name) {
    this.playAnimation(name, false);
  }

  /**
   * Plays an animation; call it every tick.
   *
   * @param name the animation's name
   * @param once whether to stop at the last frame instead of starting over
   *
   * @example.folder AnimatedSpritePlayAnimation
   */
  public void playAnimation(String name, boolean once) {
    int[] frames = this.animations.get(name);
    if (frames == null || frames.length == 0) {
      return;
    }
    double now = Game.currentOrNull() == null ? 0 : Game.current().getTime();
    if (!name.equals(this.animation)) {
      this.animation = name;
      this.animationFrame = 0;
      this.animationDone = false;
      this.animationLastStep = now;
    } else if (now - this.animationLastStep >= this.animationInterval - 1e-9 && !this.animationDone) {
      this.animationLastStep = now;
      if (this.animationFrame + 1 < frames.length) {
        this.animationFrame++;
      } else if (once) {
        this.animationDone = true;
      } else {
        this.animationFrame = 0;
      }
    }
    this.currentCostume = frames[this.animationFrame];
  }

  /**
   * @param frame the frame of the current animation to show next, counting from 0
   *
   * @example.folder AnimatedSpriteSetAnimationFrame
   */
  public void setAnimationFrame(int frame) {
    int[] frames = this.animation == null ? null : this.animations.get(this.animation);
    this.animationFrame = frames == null ? Math.max(0, frame) : Math.floorMod(frame, frames.length);
    if (frames != null) {
      this.currentCostume = frames[this.animationFrame];
    }
  }

  /**
   * Starts the current animation from its first frame again.
   *
   * @example.folder AnimatedSpriteResetAnimation
   */
  public void resetAnimation() {
    this.animationFrame = 0;
    this.animationDone = false;
    this.animation = null;
  }

  /**
   * @param seconds how long each frame shows
   *
   * @example.folder AnimatedSpriteSetAnimationInterval
   */
  public void setAnimationInterval(double seconds) {
    this.animationInterval = Math.max(0, seconds);
  }

  /**
   * @return how long each frame shows, in seconds
   *
   * @example.folder AnimatedSpriteGetAnimationInterval
   */
  public double getAnimationInterval() {
    return this.animationInterval;
  }

  /**
   * @return the frame of the current animation, counting from 0
   *
   * @example.folder AnimatedSpriteGetAnimationFrame
   */
  public int getAnimationFrame() {
    return this.animationFrame;
  }

  /**
   * @return whether an animation played with {@code once} reached its last frame
   *
   * @example.folder AnimatedSpriteIsAnimationPlayed
   */
  public boolean isAnimationPlayed() {
    return this.animationDone;
  }

  /**
   * Sets the point of the costumes the sprite turns around and stands on, in costume pixels from
   * the top left corner. Without it, that is the centre of each costume.
   *
   * @param x pixels from the left edge
   * @param y pixels from the top edge
   *
   * @example.files SpriteSetRotationCenter.java
   */
  public void setRotationCenter(double x, double y) {
    this.rotationCenter = new double[] {x, y};
  }

  // ---- sound ----------------------------------------------------------------------

  /**
   * Adds a sound (WAV or OGG).
   *
   * @param name the sound's name, for {@link #playSound(String)}
   * @param soundPath the sound file
   *
   * @example.files SpriteAddSound.java
   */
  public void addSound(String name, String soundPath) {
    this.sounds.add(name, soundPath);
  }

  /**
   * Adds a sound, named after its file.
   *
   * @param soundPath the sound file
   *
   * @example.files SpriteAddSound.java
   */
  public void addSound(String soundPath) {
    String file = soundPath.substring(Math.max(soundPath.lastIndexOf('/'), soundPath.lastIndexOf('\\')) + 1);
    int dot = file.lastIndexOf('.');
    this.addSound(dot > 0 ? file.substring(0, dot) : file, soundPath);
  }

  /**
   * Starts a sound, like Scratch's "start sound"; a sound that is still playing is not started
   * again.
   *
   * @param name the sound's name
   *
   * @scratchblock start sound [name v]
   * @example.files SpritePlaySound.java
   */
  public void playSound(String name) {
    this.sounds.play(name);
  }

  /**
   * Starts another copy of a sound, which plays alongside the others: for shots, steps, coins.
   *
   * @param name the sound's name
   * @param volume the copy's volume from 0 to 100
   * @return the copy, to stop it or change its volume
   *
   * @example.files SoundPlaybackExample.java
   */
  public SoundPlayback playSoundCopy(String name, double volume) {
    return new SoundPlayback(this.sounds.playCopy(name, volume));
  }

  /**
   * @param name the sound to stop
   *
   * @example.files SpriteStopSound.java
   */
  public void stopSound(String name) {
    this.sounds.stop(name);
  }

  /**
   * Stops every sound this object started.
   *
   * @scratchblock stop all sounds
   * @example.files SpriteStopAllSounds.java
   */
  public void stopAllSounds() {
    this.sounds.stopAll();
  }

  /**
   * @param name a sound's name
   * @return whether it is playing
   *
   * @example.files SpriteIsSoundPlaying.java
   */
  public boolean isSoundPlaying(String name) {
    return this.sounds.isPlaying(name);
  }

  /**
   * @param volume this object's volume from 0 to 100
   *
   * @scratchblock set volume to (percent) %
   * @example.files SpriteSetVolume.java
   */
  public void setVolume(double volume) {
    this.sounds.setVolume(volume);
  }

  /**
   * @param by how much to change the volume
   *
   * @scratchblock change volume by (step)
   * @example.files SpriteChangeVolume.java
   */
  public void changeVolume(double by) {
    this.sounds.setVolume(this.sounds.getVolume() + by);
  }

  /**
   * @return this object's volume from 0 to 100
   *
   * @scratchblock (volume)
   * @example.files SpriteGetVolume.java
   */
  public double getVolume() {
    return this.sounds.getVolume();
  }

  // ---- looks -------------------------------------------------------------------

  /**
   * Shows the sprite.
   *
   * @scratchblock show
   * @example.files SpriteShow.java
   */
  public void show() {
    this.visible = true;
  }

  /**
   * Hides the sprite: it is not drawn, cannot be touched and cannot be clicked.
   *
   * @scratchblock hide
   * @example.files SpriteHide.java
   */
  public void hide() {
    this.visible = false;
  }

  /**
   * @return whether the sprite is shown
   *
   * @example.files SpriteIsVisible.java
   */
  public boolean isVisible() {
    return this.visible;
  }

  /**
   * @return the size in percent of the costume's own size
   *
   * @scratchblock (size)
   * @example.files SpriteGetSize.java
   */
  public double getSize() {
    return this.size;
  }

  /**
   * @param percent the size in percent of the costume's own size
   *
   * @scratchblock set size to (percentage) %
   * @example.files SpriteSetSize.java
   */
  public void setSize(double percent) {
    this.size = Math.max(0, percent);
  }

  /**
   * @param percent how much to change the size, in percentage points
   *
   * @scratchblock change size by (amount)
   * @example.files SpriteChangeSize.java
   */
  public void changeSize(double percent) {
    this.setSize(this.size + percent);
  }

  /**
   * @return the width the sprite is drawn at, in stage pixels
   *
   * @example.files SpriteGetWidth.java
   */
  public double getWidth() {
    Costume c = this.costume();
    return this.width > 0 ? this.width : c == null ? 0 : c.image().getWidth() * this.size / 100;
  }

  /**
   * @return the height the sprite is drawn at, in stage pixels
   *
   * @example.files SpriteGetHeight.java
   */
  public double getHeight() {
    Costume c = this.costume();
    return this.height > 0 ? this.height : c == null ? 0 : c.image().getHeight() * this.size / 100;
  }

  /**
   * Draws the sprite this wide, whatever its costume's width and size: for buttons, panels and
   * platforms. With {@link #setNineSlice(int, int, int, int)} the corners keep their size.
   *
   * @param width the width in stage pixels; 0 to follow the costume and size again
   *
   * @example.files UISpriteSetWidth.java
   */
  public void setWidth(double width) {
    this.width = Math.max(0, width);
  }

  /**
   * Draws the sprite this high; see {@link #setWidth(double)}.
   *
   * @param height the height in stage pixels; 0 to follow the costume and size again
   *
   * @example.files UISpriteSetHeight.java
   */
  public void setHeight(double height) {
    this.height = Math.max(0, height);
  }

  /**
   * @param by how much wider to draw the sprite
   *
   * @example.files UISpriteChangeWidth.java
   */
  public void changeWidth(double by) {
    this.setWidth(this.getWidth() + by);
  }

  /**
   * @param by how much higher to draw the sprite
   *
   * @example.files UISpriteChangeHeight.java
   */
  public void changeHeight(double by) {
    this.setHeight(this.getHeight() + by);
  }

  /**
   * Stretches only the middle of the costume when the sprite is drawn at its own width and
   * height: the four corners keep their size (scaled with the sprite's size), the edges stretch
   * along one side.
   *
   * @param top costume pixels at the top that keep their height
   * @param right costume pixels at the right that keep their width
   * @param bottom costume pixels at the bottom that keep their height
   * @param left costume pixels at the left that keep their width
   *
   * @example.files UISpriteSetNineSlice.java
   */
  public void setNineSlice(int top, int right, int bottom, int left) {
    this.nineSlice = new int[] {left, top, right, bottom};
  }

  /**
   * Stretches the whole costume again.
   *
   * @example.files UISpriteDisableNineSlice.java
   */
  public void disableNineSlice() {
    this.nineSlice = null;
  }

  /**
   * Scratch's colour effect: shifts every colour of the costume around the colour wheel. 0 is no
   * change; 100 is halfway round; 200 is all the way round again. Like Scratch, it gives colours
   * too grey or too dark to show a hue a little one, so white and black are tinted too.
   *
   * @param value the effect
   *
   * @example.files SpriteSetColorEffect.java
   * @scratchblock set [color v] effect to (value)
   */
  public void setColorEffect(double value) {
    this.colorEffect = value;
  }

  /**
   * @param by how much to change the colour effect
   *
   * @example.files SpriteChangeColorEffect.java
   * @scratchblock change [color v] effect by (by)
   */
  public void changeColorEffect(double by) {
    this.colorEffect += by;
  }

  /** @return the colour effect */
  public double getColorEffect() {
    return this.colorEffect;
  }

  /**
   * Multiplies the costume by a colour: white leaves it as it is, red keeps only its red. Not a
   * Scratch block: Scratch's colour effect is {@link #setColorEffect(double)}.
   *
   * @param color the tint, or null for none
   *
   * @example.files SpriteSetTint.java
   */
  public void setTint(Color color) {
    this.tint = color;
  }

  /**
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   *
   * @example.files SpriteSetTint.java
   */
  public void setTint(double red, double green, double blue) {
    this.tint = new Color(red, green, blue);
  }

  /**
   * @return the tint, or null for none
   *
   * @example.files SpriteGetTint.java
   */
  public Color getTint() {
    return this.tint;
  }

  /**
   * Scratch's ghost effect.
   *
   * @param percent 0 opaque, 100 invisible
   *
   * @example.files SpriteSetTransparency.java
   * @scratchblock set [ghost v] effect to (transparency)
   */
  public void setTransparency(double percent) {
    this.transparency = Math.max(0, Math.min(100, percent));
  }

  /**
   * @param by how much to change the transparency
   *
   * @example.files SpriteChangeTransparency.java
   * @scratchblock change [ghost v] effect by (step)
   */
  public void changeTransparency(double by) {
    this.setTransparency(this.transparency + by);
  }

  /**
   * @return the transparency in percent
   *
   * @example.files SpriteGetTransparency.java
   */
  public double getTransparency() {
    return this.transparency;
  }

  Effects effects() {
    return new Effects(this.colorEffect, this.transparency, this.tint == null ? 0xffffffff : this.tint.toARGB());
  }

  // ---- layers ------------------------------------------------------------------

  /**
   * Moves the sprite to another layer, in front of the sprites already there. A sprite in
   * {@link Layer#UI} ignores the camera; its pen moves to the UI layer too.
   *
   * @param layer the layer
   */
  public void setLayer(Layer layer) {
    Layer old = this.layer;
    this.layer = layer;
    if (layer == Layer.UI && this.pen.getLayer() == Layer.BACKGROUND) {
      this.pen.setLayer(Layer.UI);
    } else if (old == Layer.UI && layer != Layer.UI && this.pen.getLayer() == Layer.UI) {
      this.pen.setLayer(Layer.BACKGROUND);
    }
    if (this.stage != null && old != layer) {
      this.stage.moveToLayer(this, old);
    }
  }

  /** @return the sprite's layer */
  public Layer getLayer() {
    return this.layer;
  }

  /**
   * Moves the sprite in front of every other sprite of its layer.
   *
   * @example.files SpriteGoToFrontLayer.java
   * @scratchblock go to [front v] layer
   */
  public void goToFrontLayer() {
    if (this.stage != null) {
      this.stage.reorder(this, Integer.MAX_VALUE);
    }
  }

  /**
   * Moves the sprite behind every other sprite of its layer.
   *
   * @example.files SpriteGoToBackLayer.java
   * @scratchblock go to [back v] layer
   */
  public void goToBackLayer() {
    if (this.stage != null) {
      this.stage.reorder(this, Integer.MIN_VALUE);
    }
  }

  /**
   * @param number how many sprites of its layer to move in front of
   *
   * @example.files SpriteGoLayersForwards.java
   * @scratchblock go [forward v] (number) layers
   */
  public void goLayersForwards(int number) {
    if (this.stage != null) {
      this.stage.reorder(this, number);
    }
  }

  /**
   * @param number how many sprites of its layer to move behind
   *
   * @example.files SpriteGoLayersBackwards.java
   * @scratchblock go [backward v] (number) layers
   */
  public void goLayersBackwards(int number) {
    this.goLayersForwards(-number);
  }

  // ---- motion -------------------------------------------------------------------

  /**
   * @return x of the sprite's position
   *
   * @example.files SpriteGetX.java
   * @scratchblock (x position)
   */
  public double getX() {
    return this.x;
  }

  /**
   * @return y of the sprite's position
   *
   * @example.files SpriteGetY.java
   * @scratchblock (y position)
   */
  public double getY() {
    return this.y;
  }

  /**
   * @return the sprite's position
   *
   * @example.files SpriteGetPosition.java
   */
  public Vector2 getPosition() {
    return new Vector2(this.x, this.y);
  }

  /**
   * Goes to a position; with the pen down, it draws a line there.
   *
   * @param x x of the new position
   * @param y y of the new position
   *
   * @scratchblock go to x: (x) y: (y)
   * @example.files SpriteSetPosition.java
   */
  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
    this.pen.setPosition(x, y);
  }

  /**
   * @param position the new position
   *
   * @scratchblock go to x: (x) y: (y)
   * @example.files SpriteSetPosition.java
   */
  public void setPosition(Vector2 position) {
    this.setPosition(position.getX(), position.getY());
  }

  /**
   * @param x the new x
   *
   * @scratchblock set x to (x)
   * @example.files SpriteSetX.java
   */
  public void setX(double x) {
    this.setPosition(x, this.y);
  }

  /**
   * @param y the new y
   *
   * @scratchblock set y to (y)
   * @example.files SpriteSetY.java
   */
  public void setY(double y) {
    this.setPosition(this.x, y);
  }

  /**
   * @param dx how far to move along x
   *
   * @scratchblock change x by (x)
   * @example.files SpriteChangeX.java
   */
  public void changeX(double dx) {
    this.setPosition(this.x + dx, this.y);
  }

  /**
   * @param dy how far to move along y
   *
   * @scratchblock change y by (y)
   * @example.files SpriteChangeY.java
   */
  public void changeY(double dy) {
    this.setPosition(this.x, this.y + dy);
  }

  /**
   * @param dx how far to move along x
   * @param dy how far to move along y
   *
   * @example.files SpriteChangePosition.java
   */
  public void changePosition(double dx, double dy) {
    this.setPosition(this.x + dx, this.y + dy);
  }

  /**
   * @param by how far to move
   *
   * @example.files SpriteChangePosition.java
   */
  public void changePosition(Vector2 by) {
    this.changePosition(by.getX(), by.getY());
  }

  /**
   * Moves forward in the sprite's direction.
   *
   * @param steps how far, in stage pixels (negative moves backwards)
   *
   * @scratchblock move (steps) steps
   * @example.files SpriteMove.java
   */
  public void move(double steps) {
    double[] d = heading(this.direction);
    this.setPosition(this.x + steps * d[0], this.y + steps * d[1]);
  }

  /**
   * The unit step of a direction (0 up, 90 right), exact at the four main directions: moving
   * right does not drift 6e-16 up (6.x printed 0.0 there, and so does Scratch).
   */
  static double[] heading(double direction) {
    double d = ((direction % 360) + 360) % 360;
    if (d == 0) {
      return new double[] {0, 1};
    } else if (d == 90) {
      return new double[] {1, 0};
    } else if (d == 180) {
      return new double[] {0, -1};
    } else if (d == 270) {
      return new double[] {-1, 0};
    }
    double radians = Math.toRadians(d);
    return new double[] {Math.sin(radians), Math.cos(radians)};
  }

  /**
   * @return the direction, from 0 (inclusive) to 360 (exclusive); 90 is right
   *
   * @example.files SpriteGetDirection.java
   * @scratchblock (direction)
   */
  public double getDirection() {
    return this.direction;
  }

  /**
   * Points in a direction: 0 up, 90 right, 180 down, 270 left; other numbers go around.
   *
   * @param degrees the direction
   *
   * @scratchblock point in direction (degrees)
   * @example.files SpriteSetDirection.java
   */
  public void setDirection(double degrees) {
    double d = degrees % 360;
    this.direction = d < 0 ? d + 360 : d;
  }

  /**
   * @param degrees how far to turn clockwise
   *
   * @scratchblock turn right (degrees) degrees
   * @example.files SpriteTurnRight.java
   */
  public void turnRight(double degrees) {
    this.setDirection(this.direction + degrees);
  }

  /**
   * @param degrees how far to turn anticlockwise
   *
   * @scratchblock turn left (degrees) degrees
   * @example.files SpriteTurnLeft.java
   */
  public void turnLeft(double degrees) {
    this.setDirection(this.direction - degrees);
  }

  /**
   * Points towards a position.
   *
   * @param position where to point
   */
  public void pointTowards(Vector2 position) {
    double dx = position.getX() - this.x, dy = position.getY() - this.y;
    if (dx != 0 || dy != 0) {
      this.setDirection(new Vector2(dx, dy).direction());
    }
  }

  /** @param sprite the sprite to point at */
  public void pointTowards(Sprite sprite) {
    this.pointTowards(sprite.getPosition());
  }

  /**
   * Points towards the mouse pointer.
   *
   * @example.files SpritePointTowardsMousePointer.java
   * @scratchblock point towards [mouse-pointer v]
   */
  public void pointTowardsMousePointer() {
    this.pointTowards(this.getMouse());
  }

  /**
   * @param style how the costume follows the direction
   *
   * @example.files SpriteSetRotationStyle.java
   * @scratchblock set rotation style [left-right v]
   */
  public void setRotationStyle(RotationStyle style) {
    this.rotationStyle = style;
  }

  /** @return how the costume follows the direction */
  public RotationStyle getRotationStyle() {
    return this.rotationStyle;
  }

  /**
   * Goes to a random position on the stage.
   *
   * @scratchblock go to [random position v]
   * @example.files SpriteGoToRandomPosition.java
   */
  public void goToRandomPosition() {
    Stage stage = this.stageOrThrow("goToRandomPosition");
    this.setPosition(Random.randomInt(-stage.getWidth() / 2, stage.getWidth() / 2),
        Random.randomInt(-stage.getHeight() / 2, stage.getHeight() / 2));
  }

  /**
   * Goes to the mouse pointer.
   *
   * @scratchblock go to [mouse-pointer v]
   * @example.files SpriteGoToMousePointer.java
   */
  public void goToMousePointer() {
    this.setPosition(this.getMouse());
  }

  /**
   * @param sprite the sprite whose position to go to
   *
   * @scratchblock go to [sprite v]
   * @example.files SpriteGoToSprite.java
   */
  public void goToSprite(Sprite sprite) {
    this.setPosition(sprite.getPosition());
  }

  /**
   * Glides to a position in a number of seconds of game time; the sprite keeps running its code
   * meanwhile. A new glide replaces one that is still going.
   *
   * @param seconds how long the glide takes
   * @param x x of the target
   * @param y y of the target
   *
   * @scratchblock glide (seconds) secs to x: (x) y: (y)
   * @example.files SpriteGlide.java
   */
  public void glide(double seconds, double x, double y) {
    this.glideFromX = this.x;
    this.glideFromY = this.y;
    this.glideToX = x;
    this.glideToY = y;
    this.glideTicks = Math.max(1, (int) Math.round(seconds * 60));
    this.glideTick = 0;
  }

  /**
   * @return whether a glide is still going
   *
   * @example.files SpriteIsGliding.java
   */
  public boolean isGliding() {
    return this.glideTick >= 0;
  }

  /** One tick of the glide, before run(). */
  void advanceGlide() {
    if (this.glideTick < 0) {
      return;
    }
    this.glideTick++;
    double t = Math.min(1, this.glideTick / (double) this.glideTicks);
    this.setPosition(this.glideFromX + (this.glideToX - this.glideFromX) * t,
        this.glideFromY + (this.glideToY - this.glideFromY) * t);
    if (t >= 1) {
      this.glideTick = -1;
    }
  }

  /**
   * Turns around when touching an edge of the stage and moves back inside, as in Scratch.
   *
   * @example.files SpriteIfOnEdgeBounce.java
   * @scratchblock if on edge, bounce
   */
  public void ifOnEdgeBounce() {
    Stage stage = this.stage;
    if (stage == null) {
      return;
    }
    Bounds b = this.getHitbox() == null ? null : this.getHitbox().getBounds();
    if (b == null) {
      return;
    }
    double halfW = stage.getWidth() / 2.0, halfH = stage.getHeight() / 2.0;
    double left = halfW + b.x(), right = halfW - (b.x() + b.width());
    double bottom = halfH + b.y(), top = halfH - (b.y() + b.height());
    double nearest = Math.min(Math.min(left, right), Math.min(top, bottom));
    if (nearest > 0) {
      return;
    }
    double[] d = heading(this.direction);
    double dx = d[0], dy = d[1];
    if (nearest == left) {
      dx = Math.max(0.2, Math.abs(dx));
    } else if (nearest == right) {
      dx = -Math.max(0.2, Math.abs(dx));
    } else if (nearest == top) {
      dy = -Math.max(0.2, Math.abs(dy));
    } else {
      dy = Math.max(0.2, Math.abs(dy));
    }
    this.setDirection(new Vector2(dx, dy).direction());
    // back inside, as Scratch's keepInFence does
    double moveX = 0, moveY = 0;
    if (left < 0) {
      moveX = -left;
    } else if (right < 0) {
      moveX = right;
    }
    if (bottom < 0) {
      moveY = -bottom;
    } else if (top < 0) {
      moveY = top;
    }
    this.setPosition(this.x + moveX, this.y + moveY);
  }

  // ---- where the costume is: one transform for drawing, hitboxes, stamps and sensing ----

  /** Whether the left-right rotation style shows the costume mirrored. */
  boolean isMirrored() {
    return this.rotationStyle == RotationStyle.LEFT_RIGHT && this.direction > 180;
  }

  /** The costume's rotation centre in its pixels. */
  double[] origin(Costume c) {
    return this.rotationCenter != null ? this.rotationCenter
        : new double[] {c.image().getWidth() / 2.0, c.image().getHeight() / 2.0};
  }

  /** Stretch of the costume: its own width and height if set, else the size in percent. */
  double[] scale(Costume c) {
    double s = this.size / 100;
    double sx = this.width > 0 ? this.width / c.image().getWidth() : s;
    double sy = this.height > 0 ? this.height / c.image().getHeight() : s;
    return new double[] {sx, sy};
  }

  /** Clockwise turn of the costume in degrees: the direction minus 90 when it turns all around. */
  double drawnRotation() {
    return this.rotationStyle == RotationStyle.ALL_AROUND ? this.direction - 90 : 0;
  }

  /**
   * (getHitbox caches the world hitbox on this method's inputs: keep them in step with it.)
   *
   * The affine map from costume pixels (origin top left, y down) to world coordinates (y up):
   * {@code x' = a·u + c·v + e}, {@code y' = b·u + d·v + f} as {a, b, c, d, e, f}.
   */
  double[] costumeToWorld(Costume c) {
    double[] o = this.origin(c);
    double[] scale = this.scale(c);
    double sx = this.isMirrored() ? -scale[0] : scale[0], sy = scale[1];
    double r = Math.toRadians(this.drawnRotation());
    double cos = Math.cos(r), sin = Math.sin(r);
    double a = sx * cos, b = -sx * sin, cc = -sy * sin, d = -sy * cos;
    return new double[] {a, b, cc, d, this.x - a * o[0] - cc * o[1], this.y - b * o[0] - d * o[1]};
  }

  /**
   * @return the sprite's shaders
   *
   * @example.files SpriteGetShaders.java
   */
  public Shaders getShaders() {
    return this.shaders;
  }

  SpriteDraw toDraw() {
    Costume c = this.costume();
    if (!this.visible || c == null) {
      return null;
    }
    double[] o = this.origin(c);
    double[] scale = this.scale(c);
    boolean ownSize = this.width > 0 || this.height > 0;
    return new SpriteDraw(c.image(), this.x, this.y, o[0], o[1], this.isMirrored() ? -scale[0] : scale[0],
        scale[1], this.drawnRotation(), this.effects(), ownSize && this.nineSlice != null ? this.nineSlice.clone() : null,
        this.size / 100, this.getWidth(), this.getHeight(), this.shaders.toDraw());
  }

  // ---- hitbox and sensing ---------------------------------------------------------

  /**
   * Sets the hitbox: the shape that counts for touching, in costume pixels (origin at the
   * costume's top left corner, y down). It turns, grows and mirrors with the sprite.
   *
   * @param shape the hitbox
   *
   * @example.files SpriteSetHitbox.java
   */
  public void setHitbox(Shape shape) {
    this.hitbox = shape;
  }

  /**
   * The rectangle around the painted pixels of the costume where the sprite is now, whatever
   * hitbox it has: bubbles hang off it (a hitbox set to the feet would put them at the knees).
   */
  Shape paintedOutline() {
    Costume c = this.costume();
    if (c == null || !this.visible) {
      return null;
    }
    double[] m = this.costumeToWorld(c);
    return Shape.of(Geometry.transform(c.outline(), m[0], m[1], m[2], m[3], m[4], m[5]));
  }

  /**
   * Where the costume's painted pixels are in the world, or null: only there can the sprite show a
   * colour. Without a hitbox of its own that is its (cached) hitbox.
   */
  Bounds paintedBounds() {
    Shape s = this.hitbox == null ? this.getHitbox() : this.paintedOutline();
    return s == null ? null : s.getBounds();
  }

  /**
   * Touching ignores this sprite until {@link #enableHitbox()}.
   *
   * @example.files SpriteDisableHitbox.java
   */
  public void disableHitbox() {
    this.hitboxDisabled = true;
  }

  /**
   * Touching counts this sprite again.
   *
   * @example.files SpriteEnableHitbox.java
   */
  public void enableHitbox() {
    this.hitboxDisabled = false;
  }

  /**
   * The hitbox where the sprite is now, in stage coordinates: by default the rectangle around the
   * costume's painted pixels; null for a sprite without costumes.
   *
   * @return the hitbox, or null
   *
   * @example.files SpriteGetHitbox.java
   */
  public Shape getHitbox() {
    Costume c = this.costume();
    if (this.hitbox == null && c == null) {
      return null;
    }
    // touching checks ask for it once per pair: made again only when one of the inputs of
    // costumeToWorld / framelessToWorld (or the costume, or the hitbox) changed
    double[] k = this.worldHitboxKey;
    if (this.worldHitbox != null && c == this.worldHitboxCostume && this.hitbox == this.worldHitboxLocal
        && this.rotationCenter == this.worldHitboxCentre && this.rotationStyle == this.worldHitboxStyle
        && this.x == k[0] && this.y == k[1] && this.direction == k[2] && this.size == k[3]
        && this.width == k[4] && this.height == k[5]) {
      return this.worldHitbox;
    }
    double[] m = c != null ? this.costumeToWorld(c) : this.framelessToWorld();
    double[] local = this.hitbox != null ? this.hitbox.getPoints() : c.outline();
    this.worldHitbox = Shape.of(Geometry.transform(local, m[0], m[1], m[2], m[3], m[4], m[5]));
    this.worldHitboxCostume = c;
    this.worldHitboxLocal = this.hitbox;
    this.worldHitboxCentre = this.rotationCenter;
    this.worldHitboxStyle = this.rotationStyle;
    this.worldHitboxKey = new double[] {this.x, this.y, this.direction, this.size, this.width, this.height};
    return this.worldHitbox;
  }

  /**
   * Where a hitbox without a costume lands: the sprite's position is the frame's origin (a
   * costume's top-left with its rotation centre there), y running down like a costume's pixels.
   * Map sprites work this way: their area is their hitbox, and they are put where their object
   * is.
   */
  private double[] framelessToWorld() {
    double s = this.size / 100;
    double sx = this.isMirrored() ? -s : s;
    double r = Math.toRadians(this.drawnRotation());
    double cos = Math.cos(r), sin = Math.sin(r);
    return new double[] {sx * cos, -sx * sin, -s * sin, -s * cos, this.x, this.y};
  }

  /** The hitbox that counts for touching: none when hidden, disabled, or without a hitbox and costume. */
  Shape touchHitbox() {
    return !this.visible || this.hitboxDisabled ? null : this.getHitbox();
  }

  /**
   * @param sprite another sprite
   * @return whether the two hitboxes overlap (touching edges do not count); a hidden sprite
   *     touches nothing, and the UI layer never touches the world
   *
   * @scratchblock &lt;touching [sprite v]?&gt;
   * @example.files SpriteIsTouchingSprite.java
   */
  public boolean isTouchingSprite(Sprite sprite) {
    if (sprite == this || sprite.stage != this.stage || (sprite.layer == Layer.UI) != (this.layer == Layer.UI)) {
      return false;
    }
    Shape mine = this.touchHitbox(), theirs = sprite.touchHitbox();
    return mine != null && theirs != null && mine.intersects(theirs);
  }

  /**
   * @param type a class of sprites
   * @return whether the sprite touches any sprite of that class (or a subclass)
   *
   * @scratchblock &lt;touching [sprite v]?&gt;
   * @example.files SpriteIsTouchingSprite.java
   */
  public boolean isTouchingSprite(Class<? extends Sprite> type) {
    return this.getTouchingSprite(type) != null;
  }

  /**
   * @param type a class of sprites
   * @param <T> the class
   * @return a sprite of that class the sprite touches, or null
   *
   * @example.files SpriteGetTouchingSprite.java
   */
  public <T extends Sprite> T getTouchingSprite(Class<T> type) {
    if (this.stage == null) {
      return null;
    }
    for (Sprite s : this.stage.spritesInOrder()) {
      if (type.isInstance(s) && this.isTouchingSprite(s)) {
        return type.cast(s);
      }
    }
    return null;
  }

  // nrw-standard-begin
  /**
   * @param type a class of sprites
   * @param <T> the class
   * @return every sprite of that class the sprite touches
   *
   * @example.files SpriteGetTouchingSprites.java
   */
  public <T extends Sprite> List<T> getTouchingSprites(Class<T> type) {
    List<T> touching = new ArrayList<>();
    if (this.stage != null) {
      for (Sprite s : this.stage.spritesInOrder()) {
        if (type.isInstance(s) && this.isTouchingSprite(s)) {
          touching.add(type.cast(s));
        }
      }
    }
    return touching;
  }
  // nrw-standard-end
  // nrw: /**
  // nrw:  * Every sprite of a class the sprite touches, in the {@code List} of the NRW Zentralabitur,
  // nrw:  * which has to be in your project next to your own classes.
  // nrw:  *
  // nrw:  * @param type a class of sprites
  // nrw:  * @param <T> the class
  // nrw:  * @param <L> your {@code List} type
  // nrw:  * @return a new list of the sprites touched
  // nrw:  */
  // nrw: public <T extends Sprite, L> L getTouchingSprites(Class<T> type) {
  // nrw:   List<T> touching = new ArrayList<>();
  // nrw:   if (this.stage != null) {
  // nrw:     for (Sprite s : this.stage.spritesInOrder()) {
  // nrw:       if (type.isInstance(s) && this.isTouchingSprite(s)) {
  // nrw:         touching.add(type.cast(s));
  // nrw:       }
  // nrw:     }
  // nrw:   }
  // nrw:   return org.openpatch.scratch.internal.NrwList.of(touching, type, this.getClass());
  // nrw: }

  /**
   * The costume on the screen for the compositor, or null when not shown.
   *
   * @param withGhost false for the sensing sprite itself: its own ghost effect does not hide it
   */
  Compositor.Picture picture(boolean withGhost) {
    Costume c = this.costume();
    if (!this.visible || c == null || this.stage == null || this.layer == Layer.UI) {
      return null;
    }
    Effects e = this.effects();
    if (!withGhost) {
      e = new Effects(e.colorEffect(), 0, e.tint());
    }
    int[] slice = this.width > 0 || this.height > 0 ? this.nineSlice : null;
    if (slice == null) {
      return new Compositor.Picture(c.image(), this.stage.toScreen(this.costumeToWorld(c)), e);
    }
    // the renderer's cuts (SpriteDraw.slices), in evenly stretched costume pixels
    int iw = c.image().getWidth(), ih = c.image().getHeight();
    double[] scale = this.scale(c);
    return new Compositor.Picture(c.image(), this.stage.toScreen(this.costumeToWorld(c)), e,
        cuts(SpriteDraw.slices(iw, slice[0], slice[2], iw * scale[0], this.size / 100), scale[0], iw),
        cuts(SpriteDraw.slices(ih, slice[1], slice[3], ih * scale[1], this.size / 100), scale[1], ih));
  }

  private static double[][] cuts(double[][] slices, double scale, int size) {
    double[] from = slices[0].clone(), to = slices[1].clone();
    for (int i = 0; i < from.length; i++) {
      from[i] /= scale;
      to[i] *= size;
    }
    return new double[][] {from, to};
  }

  /**
   * Whether the sprite touches a colour: a painted pixel of the sprite lies over that colour, as
   * the stage shows it now (backdrop, stage colour, pen strokes, stamps and other sprites with
   * their effects; not texts, shaders or the UI layer). Colours match as in Scratch, with a small
   * tolerance.
   *
   * @param color the colour to look for
   * @return whether the sprite touches it
   *
   * @example.files SpriteIsTouchingColor.java
   * @scratchblock &lt;touching color [#ff0000]?&gt;
   */
  public boolean isTouchingColor(Color color) {
    Compositor.Picture self = this.picture(false);
    return self != null && this.stage.compositor(this).touches(self, null, color.toARGB());
  }

  /**
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   * @return whether the sprite touches that colour
   *
   * @example.files SpriteIsTouchingColor.java
   * @scratchblock &lt;touching color [#ff0000]?&gt;
   */
  public boolean isTouchingColor(double red, double green, double blue) {
    return this.isTouchingColor(new Color(red, green, blue));
  }

  /**
   * Whether a colour of the sprite touches a colour on the stage, like Scratch's "color is
   * touching color".
   *
   * @param mine a colour of the sprite
   * @param color the colour to look for underneath
   * @return whether they touch
   *
   * @example.files SpriteIsColorTouchingColor.java
   * @scratchblock &lt;color [#ff0000] is touching [#00ff00]?&gt;
   */
  public boolean isColorTouchingColor(Color mine, Color color) {
    Compositor.Picture self = this.picture(false);
    return self != null && this.stage.compositor(this).touches(self, mine.toARGB(), color.toARGB());
  }

  /**
   * @return whether the mouse pointer is over the sprite's hitbox
   *
   * @scratchblock &lt;touching [mouse-pointer v]?&gt;
   * @example.files SpriteIsTouchingMousePointer.java
   */
  public boolean isTouchingMousePointer() {
    Shape h = this.touchHitbox();
    Vector2 m = this.getMouse();
    return h != null && h.contains(m.getX(), m.getY());
  }

  /**
   * @return whether the sprite's hitbox reaches an edge of the stage
   *
   * @example.files SpriteIsTouchingEdge.java
   * @scratchblock &lt;touching [edge v]?&gt;
   */
  public boolean isTouchingEdge() {
    Shape h = this.touchHitbox();
    if (h == null || this.stage == null) {
      return false;
    }
    Bounds b = h.getBounds();
    double halfW = this.stage.getWidth() / 2.0, halfH = this.stage.getHeight() / 2.0;
    return b.x() <= -halfW || b.y() <= -halfH || b.x() + b.width() >= halfW || b.y() + b.height() >= halfH;
  }

  /**
   * @return the distance to the mouse pointer
   *
   * @example.files SpriteDistanceToMousePointer.java
   * @scratchblock (distance to [mouse-pointer v])
   */
  public double distanceToMousePointer() {
    return this.getMouse().distance(this.getPosition());
  }

  /**
   * @param sprite another sprite
   * @return the distance between the two positions
   *
   * @example.files SpriteDistanceToSprite.java
   * @scratchblock (distance to [sprite v])
   */
  public double distanceToSprite(Sprite sprite) {
    return sprite.getPosition().distance(this.getPosition());
  }

  /**
   * @return this object's timer, counting game time since it was made or reset
   *
   * @scratchblock (timer)
   * @example.files SpriteGetTimer.java
   */
  public Timer getTimer() {
    return this.timer;
  }

  // ---- input -------------------------------------------------------------------

  /**
   * @return x of the mouse pointer, in the sprite's coordinates (world, or the stage for UI)
   *
   * @scratchblock (mouse x)
   * @example.files SpriteGetMouseX.java
   */
  public double getMouseX() {
    return this.getMouse().getX();
  }

  /**
   * @return y of the mouse pointer
   *
   * @scratchblock (mouse y)
   * @example.files SpriteGetMouseY.java
   */
  public double getMouseY() {
    return this.getMouse().getY();
  }

  /**
   * @return the mouse pointer's position, in world coordinates (or stage coordinates for UI)
   *
   * @example.files SpriteGetMouse.java
   */
  public Vector2 getMouse() {
    Game game = Game.currentOrNull();
    if (game == null) {
      return new Vector2(0, 0);
    }
    double mx = game.getInput().getMouseX(), my = game.getInput().getMouseY();
    if (this.layer == Layer.UI || this.stage == null) {
      return new Vector2(mx, my);
    }
    Camera camera = this.stage.getCamera();
    return new Vector2(camera.toWorldX(mx), camera.toWorldY(my));
  }

  /**
   * @return whether a mouse button is held down
   *
   * @example.files SpriteIsMouseDown.java
   * @scratchblock &lt;mouse down?&gt;
   */
  public boolean isMouseDown() {
    Game game = Game.currentOrNull();
    return game != null && game.getInput().isMouseDown();
  }

  /**
   * @param key a key
   * @return whether the key is held down
   *
   * @example.files SpriteIsKeyPressed.java
   * @scratchblock &lt;key [space v] pressed?&gt;
   */
  public boolean isKeyPressed(KeyCode key) {
    Game game = Game.currentOrNull();
    return game != null && game.getInput().isKeyDown(key);
  }

  /**
   * Called when a key is pressed, once per press (holding a key down does not repeat it).
   *
   * @param key the key
   *
   * @example.files SpriteWhenKeyPressed.java
   * @scratchblock when [space v] key pressed
   */
  public void whenKeyPressed(KeyCode key) {}

  /**
   * Called when a key is released.
   *
   * @param key the key
   *
   * @example.files SpriteWhenKeyReleased.java
   */
  public void whenKeyReleased(KeyCode key) {}

  /**
   * Called when the mouse moves.
   *
   * @param x x of the mouse pointer (world)
   * @param y y of the mouse pointer (world)
   *
   * @example.files SpriteWhenMouseMoved.java
   */
  public void whenMouseMoved(double x, double y) {}

  /**
   * Called when a mouse button is pressed anywhere on the stage.
   *
   * @param button the button
   *
   * @example.files SpriteWhenMouseClicked.java
   */
  public void whenMouseClicked(MouseCode button) {}

  /**
   * Called when this sprite is clicked: a mouse button is pressed over its hitbox, and no sprite
   * in front of it is under the pointer.
   *
   * @example.files SpriteWhenClicked.java
   * @scratchblock when this sprite clicked
   */
  public void whenClicked() {}

  // ---- events and broadcasts ---------------------------------------------------------

  /**
   * Sends a message to the stage and every sprite on it; their {@link #whenIReceive(String)}
   * runs at once, before this method returns.
   *
   * @param message the message
   *
   * @scratchblock broadcast [message v]
   * @example.files SpriteBroadcast.java
   */
  public void broadcast(String message) {
    if (this.stage != null) {
      this.stage.broadcast(message);
    }
  }

  /**
   * Called when a message is broadcast.
   *
   * @param message the message
   *
   * @scratchblock when I receive [message v]
   * @example.files SpriteWhenIReceive.java
   */
  public void whenIReceive(String message) {}

  /**
   * Called when the stage switches its backdrop.
   *
   * @param name the new backdrop's name
   *
   * @example.files SpriteWhenBackdropSwitches.java
   * @scratchblock when backdrop switches to [name v]
   */
  public void whenBackdropSwitches(String name) {}

  /**
   * A random whole number between two numbers, both included.
   *
   * @param from the lowest
   * @param to the highest
   * @return the number
   *
   * @example.files SpritePickRandom.java
   * @scratchblock (pick random (from) to (to))
   */
  public int pickRandom(int from, int to) {
    return Random.randomInt(from, to);
  }

  /**
   * Runs every tick, 60 times a second of game time. Override it with the sprite's code.
   *
   * @example.files SpriteRun.java
   */
  public void run() {}

  // ---- say and think -------------------------------------------------------------

  /**
   * @return the sprite's text, which say and think show in a bubble
   *
   * @example.files SpriteGetText.java
   */
  public Text getText() {
    return this.text;
  }

  /**
   * Shows a speech bubble until the sprite says something else; an empty text removes it.
   *
   * @param text what the sprite says
   *
   * @scratchblock say [text]
   * @example.files SpriteSay.java
   */
  public void say(String text) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text);
  }

  /**
   * Shows a speech bubble for a number of seconds of game time.
   *
   * @param text what the sprite says
   * @param seconds how long
   *
   * @scratchblock say [text] for (seconds) seconds
   */
  public void say(String text, double seconds) {
    this.text.setStyle(TextStyle.SPEAK);
    this.text.showText(text, seconds);
  }

  /**
   * Shows a thought bubble until the sprite thinks something else; an empty text removes it.
   *
   * @param text what the sprite thinks
   *
   * @scratchblock think [text]
   * @example.files SpriteThink.java
   */
  public void think(String text) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text);
  }

  /**
   * Shows a thought bubble for a number of seconds of game time.
   *
   * @param text what the sprite thinks
   * @param seconds how long
   *
   * @scratchblock think [text] for (seconds) seconds
   */
  public void think(String text, double seconds) {
    this.text.setStyle(TextStyle.THINK);
    this.text.showText(text, seconds);
  }

  /**
   * Asks a question: the sprite says it in a bubble, and a box to type the answer in appears at
   * the bottom of the stage. Enter finishes it; see {@link Stage#ask(String)}.
   *
   * @param question the question
   *
   * @scratchblock ask [question] and wait
   * @example.files SpriteAsk.java
   */
  public void ask(String question) {
    if (this.stage != null) {
      this.say(question);
      this.stage.askForSprite(this, question);
    }
  }

  /**
   * @return whether a question is waiting for its answer
   *
   * @example.files SpriteIsAsking.java
   */
  public boolean isAsking() {
    return this.stage != null && this.stage.isAsking();
  }

  /**
   * @return the answer to the last question
   *
   * @scratchblock (answer)
   * @example.files SpriteGetAnswer.java
   */
  public String getAnswer() {
    return this.stage == null ? "" : this.stage.getAnswer();
  }

  // ---- clones -------------------------------------------------------------------

  /**
   * Makes a clone: a copy of this sprite with the same costumes, position, direction, effects and
   * variables, added to the stage just behind this sprite, as in Scratch. Its
   * {@link #whenStartsAsClone()} runs;
   * its constructor does not. Numbers, texts and booleans in its variables are its own; lists and
   * other objects are shared with this sprite until the clone gets its own in
   * {@link #whenStartsAsClone()}.
   *
   * <pre>{@code
   * Bullet bullet = this.createClone();
   * }</pre>
   *
   * @param <T> the sprite's class (no cast needed)
   * @return the clone
   */
  @SuppressWarnings("unchecked")
  public <T extends Sprite> T createClone() {
    Sprite copy;
    try {
      copy = (Sprite) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException(e);
    }
    copy.costumes = new ArrayList<>(this.costumes);
    copy.animations = new java.util.HashMap<>(this.animations);
    copy.animation = null;
    copy.pen = new Pen(this.pen, copy);
    copy.text = new Text(copy);
    copy.timer = new Timer();
    copy.sounds = this.sounds.copy();
    copy.shaders = new Shaders(this.shaders);
    copy.monitors = new java.util.LinkedHashMap<>(); // a sprite's monitors are not its clones'
    copy.isClone = true;
    copy.attachments = new org.openpatch.scratch.internal.ExtensionHost.Attachments();
    copy.stage = null;
    copy.glideTick = -1;
    copy.copyStateFrom(this);
    if (this.stage != null) {
      this.stage.addClone(copy, this);
    }
    copy.whenStartsAsClone();
    return (T) copy;
  }

  /** Hook for state that must not be shared with a clone; parts of the sprite added later use it. */
  void copyStateFrom(Sprite original) {}

  /**
   * Not for sprites: use {@link #createClone()}.
   *
   * @return never
   * @throws CloneNotSupportedException always
   *
   * @example.files AnimatedSpriteClone.java
   */
  @Override
  protected final Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Use createClone() to clone a sprite.");
  }

  /**
   * Called in a new clone, right after it was made.
   *
   * @scratchblock when I start as a clone
   * @example.files SpriteWhenStartsAsClone.java
   */
  public void whenStartsAsClone() {}

  /**
   * @return whether this sprite is a clone
   *
   * @example.files SpriteIsClone.java
   */
  public boolean isClone() {
    return this.isClone;
  }

  /**
   * Removes this sprite if it is a clone, stopping its sounds; does nothing for the original.
   *
   * @scratchblock delete this clone
   * @example.files SpriteDeleteThisClone.java
   */
  public void deleteThisClone() {
    if (this.isClone) {
      this.sounds.stopAll();
      this.remove();
    }
  }

  // ---- pen and stamps -----------------------------------------------------------------

  /**
   * @return the sprite's pen, which follows it
   *
   * @example.files SpriteGetPen.java
   */
  public Pen getPen() {
    return this.pen;
  }

  /**
   * Stamps the costume onto the canvas of the sprite's pen layer (behind the sprites unless the
   * pen was moved): a picture of the sprite as it looks now, effects included, that stays.
   *
   * @example.files SpriteStamp.java
   * @scratchblock stamp
   */
  public void stamp() {
    this.stamp(this.pen.getLayer());
  }

  /**
   * Stamps the costume onto the canvas of a layer.
   *
   * @param layer the layer
   *
   * @example.files SpriteStamp.java
   * @scratchblock stamp
   */
  public void stamp(Layer layer) {
    Costume c = this.costume();
    if (c != null && this.stage != null) {
      this.stage.stamp(layer, c.image(), this.costumeToWorld(c), this.effects(), this.layer == Layer.UI);
    }
  }

  // ---- helpers ------------------------------------------------------------------

  private Stage stageOrThrow(String method) {
    if (this.stage == null) {
      throw new IllegalStateException(method + "() needs the sprite to be on a stage. Add it first.");
    }
    return this.stage;
  }

  /**
   * The sprite's centre of the costume on the stage, as Scratch's "x position"/"y position".
   *
   * @example.files AnimatedSpriteToString.java
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " at (" + this.x + ", " + this.y + ")";
  }
}
