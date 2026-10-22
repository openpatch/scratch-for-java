package org.openpatch.scratch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.openpatch.scratch.internal.Costume;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.Monitor;
import org.openpatch.scratch.internal.SoundBank;
import org.openpatch.scratch.internal.canvas.Canvas;
import org.openpatch.scratch.internal.compose.Compositor;
import org.openpatch.scratch.internal.draw.AskDraw;
import org.openpatch.scratch.internal.draw.CameraDraw;
import org.openpatch.scratch.internal.draw.DrawList;
import org.openpatch.scratch.internal.draw.Effects;
import org.openpatch.scratch.internal.draw.Item;
import org.openpatch.scratch.internal.draw.LayerDraw;
import org.openpatch.scratch.internal.draw.ShapeDraw;
import org.openpatch.scratch.internal.draw.SpriteDraw;
import org.openpatch.scratch.internal.draw.TextDraw;
import org.openpatch.scratch.internal.image.Image;
import org.openpatch.scratch.internal.image.Images;
import org.openpatch.scratch.internal.input.InputEvent;
import org.openpatch.scratch.internal.text.TextLayout;

/**
 * A stage: one screen of a program, with its backdrops, sprites, texts and pens, and code that
 * runs every tick in {@link #run()}. The window shows one stage at a time
 * ({@code window.setStage(new MyStage())}).
 *
 * <pre>{@code
 * public class MyStage extends Stage {
 *   public MyStage() {
 *     this.addBackdrop("forest", "assets/forest.png");
 *     this.add(new Cat());
 *   }
 * }
 * }</pre>
 *
 * <p>A stage needs the window to exist, since the window sets its size: create the window first.
 */
public class Stage {

  static {
    Game.register(new SceneBridge());
  }

  private static final java.util.concurrent.atomic.AtomicInteger IDS = new java.util.concurrent.atomic.AtomicInteger();

  private final int id = IDS.incrementAndGet();
  private final int width;
  private final int height;
  /** Sprites per layer (Layer.ordinal()), back to front. */
  private final List<List<Sprite>> sprites = new ArrayList<>();
  private final List<Pen> pens = new ArrayList<>();
  private final List<Text> texts = new ArrayList<>();
  private final java.util.Map<String, Monitor> monitors = new java.util.LinkedHashMap<>();
  private final SoundBank sounds = new SoundBank();
  private final Canvas[] canvases = new Canvas[Layer.values().length];
  private final List<Costume> backdrops = new ArrayList<>();
  private final List<Boolean> stretched = new ArrayList<>();
  private String cursor;
  private Effects backdropEffects = Effects.NONE;
  private int cursorX, cursorY;
  private int currentBackdrop;
  private Color color = new Color(255, 255, 255);
  private final Camera camera = new Camera();
  private Comparator<? super Sprite> sorting;
  private final Timer timer = new Timer();
  private final Shaders shaders = new Shaders();
  // display: a band along the bottom of the stage, in the UI layer, above the ask box
  private final Text displayBand = new Text("", 0, 0, 0, TextStyle.BOX);
  // the ask box of the last draw list: where its check button is
  private AskDraw askBox;
  private String question;
  private Sprite askingSprite;
  private final StringBuilder typed = new StringBuilder();
  private String answer = "";

  /** A stage the size of the window. */
  public Stage() {
    Game game = Game.current();
    this.width = game.getWidth();
    this.height = game.getHeight();
    for (int i = 0; i < Layer.values().length; i++) {
      this.sprites.add(new ArrayList<>());
    }
    game.getExtensions().stageCreated(this);
    this.displayBand.setLayer(Layer.UI);
    this.displayBand.setAlign(TextAlign.LEFT);
    this.displayBand.setBandWidth(this.width - 2 * AskDraw.MARGIN);
    this.displayBand.hide();
  }

  /**
   * @return the stage's width in stage pixels
   *
   * @example.files StageGetWidth.java
   */
  public int getWidth() {
    return this.width;
  }

  /**
   * @return the stage's height in stage pixels
   *
   * @example.files StageGetHeight.java
   */
  public int getHeight() {
    return this.height;
  }

  /**
   * @return the stage's shaders, applied to the picture of its world layers
   *
   * @example.files StageGetShaders.java
   */
  public Shaders getShaders() {
    return this.shaders;
  }

  private final org.openpatch.scratch.internal.ExtensionHost.Attachments attachments =
      new org.openpatch.scratch.internal.ExtensionHost.Attachments();

  /**
   * An extension's state for this stage, made on first use. For extensions, not for learner code.
   *
   * @param type the state's class
   * @param factory makes it
   * @param <T> the state
   * @return the stage's one instance
   */
  public <T> T extension(Class<T> type, java.util.function.Supplier<T> factory) {
    return this.attachments.get(type, factory);
  }

  /**
   * @return the stage's camera
   *
   * @example.files StageGetCamera.java
   */
  public Camera getCamera() {
    return this.camera;
  }

  /**
   * Prints values for debugging, but only while debug mode is on.
   *
   * @param values what to print
   *
   * @example.files StageDebug.java
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
   * @example.files StageShowVariable.java
   */
  public void showVariable(String name, java.util.function.Supplier<?> value) {
    this.monitors.put(name, new Monitor(name, value));
  }

  /**
   * @param name the label of a variable to hide again
   *
   * @scratchblock hide variable [name v]
   * @example.files StageHideVariable.java
   */
  public void hideVariable(String name) {
    this.monitors.remove(name);
  }

  // ---- sprites -------------------------------------------------------------------

  /**
   * Adds a sprite, in front of the sprites of its layer. A sprite can only be on one stage; it
   * leaves the old one.
   *
   * @param sprite the sprite
   *
   * @example.files StageAdd.java
   */
  public void add(Sprite sprite) {
    if (sprite.getStage() == this) {
      return;
    }
    if (sprite.getStage() != null) {
      sprite.getStage().remove(sprite);
    }
    this.sprites.get(sprite.getLayer().ordinal()).add(sprite);
    sprite.setStage(this);
    sprite.getPen().jumpTo(sprite.getX(), sprite.getY());
    sprite.whenAddedToStage();
  }

  /**
   * Adds a pen of its own.
   *
   * @param pen the pen
   *
   * @example.files StageAdd.java
   */
  public void add(Pen pen) {
    if (!this.pens.contains(pen)) {
      this.pens.add(pen);
      pen.setStage(this);
    }
  }

  /**
   * Adds a text of its own.
   *
   * @param text the text
   *
   * @example.files StageAdd.java
   */
  public void add(Text text) {
    if (text.getStage() == this) {
      return;
    }
    if (text.getStage() != null) {
      text.getStage().remove(text);
    }
    this.texts.add(text);
    text.setStage(this);
    text.whenAddedToStage();
  }

  /**
   * @param text a text of its own to remove
   *
   * @example.files StageRemove.java
   */
  public void remove(Text text) {
    if (this.texts.remove(text)) {
      text.setStage(null);
      text.whenRemovedFromStage();
    }
  }

  /**
   * Removes a sprite.
   *
   * @param sprite the sprite
   *
   * @example.files StageRemove.java
   */
  public void remove(Sprite sprite) {
    if (sprite.getStage() != this) {
      return;
    }
    this.sprites.get(sprite.getLayer().ordinal()).remove(sprite);
    sprite.setStage(null);
    sprite.whenRemovedFromStage();
  }

  /**
   * @param pen a pen of its own to remove
   *
   * @example.files StageRemove.java
   */
  public void remove(Pen pen) {
    if (this.pens.remove(pen)) {
      pen.setStage(null);
    }
  }

  /**
   * Removes every sprite of a class (subclasses included).
   *
   * @param type the class
   *
   * @example.files StageRemove.java
   */
  public void remove(Class<? extends Sprite> type) {
    for (Sprite s : this.spritesInOrder()) {
      if (type.isInstance(s)) {
        this.remove(s);
      }
    }
  }

  /**
   * Removes every sprite, text and pen: an empty stage (backdrops and what was drawn stay).
   *
   * @example.files StageRemoveAll.java
   */
  public void removeAll() {
    for (Sprite s : this.spritesInOrder()) {
      this.remove(s);
    }
    for (Text t : List.copyOf(this.texts)) {
      this.remove(t);
    }
    for (Pen p : List.copyOf(this.pens)) {
      this.remove(p);
    }
  }

  // nrw-standard-begin
  /**
   * @return every sprite on the stage, back to front, layer by layer
   *
   * @example.files StageGetAll.java
   */
  public List<Sprite> getAll() {
    return this.spritesInOrder();
  }
  // nrw-standard-end
  // nrw: /**
  // nrw:  * Every sprite on the stage, back to front, layer by layer, in the {@code List} of the NRW
  // nrw:  * Zentralabitur, which has to be in your project next to your own classes.
  // nrw:  *
  // nrw:  * @param <L> your {@code List} type
  // nrw:  * @return a new list of all sprites
  // nrw:  */
  // nrw: public <L> L getAll() {
  // nrw:   return org.openpatch.scratch.internal.NrwList.of(this.spritesInOrder(), this.getClass());
  // nrw: }

  // nrw-standard-begin

  /**
   * @param type a class of sprites
   * @param <T> the class
   * @return every sprite of that class (subclasses included), back to front
   *
   * @example.files StageFind.java
   */
  public <T extends Sprite> List<T> find(Class<T> type) {
    List<T> found = new ArrayList<>();
    for (Sprite s : this.spritesInOrder()) {
      if (type.isInstance(s)) {
        found.add(type.cast(s));
      }
    }
    return found;
  }
  // nrw-standard-end
  // nrw: /**
  // nrw:  * Every sprite of a class (subclasses included), back to front, in the {@code List} of the
  // nrw:  * NRW Zentralabitur, which has to be in your project next to your own classes.
  // nrw:  *
  // nrw:  * @param type a class of sprites
  // nrw:  * @param <T> the class
  // nrw:  * @param <L> your {@code List} type
  // nrw:  * @return a new list of the sprites of that class
  // nrw:  */
  // nrw: public <T extends Sprite, L> L find(Class<T> type) {
  // nrw:   List<T> found = new ArrayList<>();
  // nrw:   for (Sprite s : this.spritesInOrder()) {
  // nrw:     if (type.isInstance(s)) {
  // nrw:       found.add(type.cast(s));
  // nrw:     }
  // nrw:   }
  // nrw:   return org.openpatch.scratch.internal.NrwList.of(found, type, this.getClass());
  // nrw: }

  /**
   * @param type a class of sprites
   * @param <T> the class
   * @return how many sprites of that class are on the stage
   *
   * @example.files StageCount.java
   */
  public <T extends Sprite> int count(Class<T> type) {
    int n = 0;
    for (List<Sprite> layer : this.sprites) {
      for (Sprite s : layer) {
        if (type.isInstance(s)) {
          n++;
        }
      }
    }
    return n;
  }

  /** A copy of all sprites in drawing (and running) order. */
  List<Sprite> spritesInOrder() {
    List<Sprite> all = new ArrayList<>();
    for (List<Sprite> layer : this.sprites) {
      all.addAll(layer);
    }
    return all;
  }

  void addClone(Sprite clone, Sprite original) {
    List<Sprite> layer = this.sprites.get(clone.getLayer().ordinal());
    // behind the original, as in Scratch
    int index = layer.indexOf(original);
    layer.add(Math.max(index, 0), clone);
    clone.setStage(this);
    clone.whenAddedToStage();
  }

  void moveToLayer(Sprite sprite, Layer from) {
    this.sprites.get(from.ordinal()).remove(sprite);
    this.sprites.get(sprite.getLayer().ordinal()).add(sprite);
  }

  /** Moves a text among the stage's texts: by {@code steps} forwards (negative: backwards). */
  void reorder(Text text, int steps) {
    int from = this.texts.indexOf(text);
    if (from < 0) {
      return;
    }
    this.texts.remove(from);
    long to = (long) from + steps;
    this.texts.add((int) Math.max(0, Math.min(this.texts.size(), to)), text);
  }

  /** Moves a sprite within its layer: by {@code steps} forwards (negative: backwards). */
  void reorder(Sprite sprite, int steps) {
    List<Sprite> layer = this.sprites.get(sprite.getLayer().ordinal());
    int from = layer.indexOf(sprite);
    if (from < 0) {
      return;
    }
    layer.remove(from);
    long to = (long) from + steps;
    layer.add((int) Math.max(0, Math.min(layer.size(), to)), sprite);
  }

  /**
   * Draws and runs the sprites of each layer sorted by their y position: lower sprites in front,
   * as in a top-down game. Applies from the next tick on, until {@link #stopSorting()}.
   */
  public void sortByY() {
    this.sorting = Comparator.comparingDouble(Sprite::getY).reversed();
  }

  /**
   * Draws and runs the sprites of each layer in the order a comparator gives, back to front.
   *
   * @param comparator the order
   */
  public void sortBy(Comparator<? super Sprite> comparator) {
    this.sorting = comparator;
  }

  /** Keeps the order the sprites have now and stops sorting them. */
  public void stopSorting() {
    this.sorting = null;
  }

  // ---- backdrops and colour ----------------------------------------------------------

  /**
   * Adds a backdrop. The first one added is shown.
   *
   * @param name the backdrop's name
   * @param imagePath the image file
   *
   * @example.files StageAddBackdrop.java
   */
  public void addBackdrop(String name, String imagePath) {
    this.addBackdrop(name, imagePath, false);
  }

  /**
   * Adds a backdrop, named after its file.
   *
   * @param imagePath the image file, or a built-in sprite's name
   *
   * @example.files StageAddBackdrop.java
   */
  public void addBackdrop(String imagePath) {
    String file = imagePath.substring(Math.max(imagePath.lastIndexOf('/'), imagePath.lastIndexOf('\\')) + 1);
    int dot = file.lastIndexOf('.');
    this.addBackdrop(dot > 0 ? file.substring(0, dot) : file, imagePath);
  }

  /**
   * Adds a backdrop, optionally stretched to the stage's size.
   *
   * @param name the backdrop's name
   * @param imagePath the image file, or a built-in sprite's name
   * @param stretch whether to stretch it to the stage's size
   *
   * @example.files StageAddBackdrop.java
   */
  public void addBackdrop(String name, String imagePath, boolean stretch) {
    for (Costume c : this.backdrops) {
      if (c.name().equals(name)) {
        return;
      }
    }
    this.backdrops.add(new Costume(name, Images.load(imagePath)));
    this.stretched.add(stretch);
  }

  /** The backdrop's costume → world map: centred on the origin, stretched if asked for. */
  private double[] backdropToWorld(int index) {
    Image image = this.backdrops.get(index).image();
    double sx = this.stretched.get(index) ? (double) this.width / image.getWidth() : 1;
    double sy = this.stretched.get(index) ? (double) this.height / image.getHeight() : 1;
    return new double[] {sx, 0, 0, -sy, -image.getWidth() * sx / 2, image.getHeight() * sy / 2};
  }

  /**
   * Shows an image instead of the mouse pointer while the mouse is over the stage.
   *
   * @param path an image file, or a built-in sprite's name
   *
   * @example.files StageSetCursor.java
   */
  public void setCursor(String path) {
    this.setCursor(path, 0, 0);
  }

  /**
   * Shows an image instead of the mouse pointer.
   *
   * @param path an image file, or a built-in sprite's name
   * @param x pixels from the image's left edge to the point that clicks
   * @param y pixels from the image's top edge to the point that clicks
   *
   * @example.files StageSetCursor.java
   */
  public void setCursor(String path, int x, int y) {
    Images.load(path); // fails now, not when the window draws
    this.cursor = path;
    this.cursorX = x;
    this.cursorY = y;
  }

  /**
   * Switches to the backdrop with that name and tells the stage and every sprite
   * ({@code whenBackdropSwitches}); an unknown name changes nothing.
   *
   * @param name the backdrop's name
   *
   * @scratchblock switch backdrop to [name v]
   * @example.files StageSwitchBackdrop.java
   */
  public void switchBackdrop(String name) {
    for (int i = 0; i < this.backdrops.size(); i++) {
      if (this.backdrops.get(i).name().equals(name)) {
        this.showBackdrop(i);
        return;
      }
    }
  }

  /**
   * Switches to the next backdrop, after the last to the first.
   *
   * @scratchblock next backdrop
   * @example.files StageNextBackdrop.java
   */
  public void nextBackdrop() {
    if (!this.backdrops.isEmpty()) {
      this.showBackdrop((this.currentBackdrop + 1) % this.backdrops.size());
    }
  }

  /**
   * Switches to the previous backdrop, before the first to the last.
   *
   * @scratchblock switch backdrop to [previous backdrop v]
   * @example.files StagePreviousBackdrop.java
   */
  public void previousBackdrop() {
    if (!this.backdrops.isEmpty()) {
      this.showBackdrop(Math.floorMod(this.currentBackdrop - 1, this.backdrops.size()));
    }
  }

  /**
   * Switches to a random backdrop other than the current one (if there is another).
   *
   * @scratchblock switch backdrop to [random backdrop v]
   * @example.files StageRandomBackdrop.java
   */
  public void randomBackdrop() {
    int n = this.backdrops.size();
    if (n > 1) {
      int pick = Random.randomInt(0, n - 2);
      this.showBackdrop(pick >= this.currentBackdrop ? pick + 1 : pick);
    }
  }

  private void showBackdrop(int index) {
    this.currentBackdrop = index;
    String name = this.backdrops.get(index).name();
    this.whenBackdropSwitches(name);
    for (Sprite s : this.spritesInOrder()) {
      s.whenBackdropSwitches(name);
    }
  }

  /**
   * @return the current backdrop's name, or null without backdrops
   *
   * @example.files StageGetCurrentBackdropName.java
   * @scratchblock (backdrop [name v])
   */
  public String getCurrentBackdropName() {
    return this.backdrops.isEmpty() ? null : this.backdrops.get(this.currentBackdrop).name();
  }

  /**
   * @return the current backdrop's number, counting from 0
   *
   * @example.files StageGetCurrentBackdropIndex.java
   * @scratchblock (backdrop [number v])
   */
  public int getCurrentBackdropIndex() {
    return this.currentBackdrop;
  }

  /**
   * Called when the backdrop switches.
   *
   * @param name the new backdrop's name
   *
   * @example.files StageWhenBackdropSwitches.java
   */
  public void whenBackdropSwitches(String name) {}

  /**
   * Scratch's colour effect on the backdrop: shifts its colours around the colour wheel (200 is
   * all the way round). Like Scratch, it gives colours too grey or too dark to show a hue a
   * little one, so white and black are tinted too.
   *
   * @param value the effect
   *
   * @example.files StageSetColorEffect.java
   * @scratchblock set [color v] effect to (value)
   */
  public void setColorEffect(double value) {
    this.backdropEffects = new Effects(value, this.backdropEffects.transparency(), this.backdropEffects.tint());
  }

  /**
   * @param by how much to change the backdrop's colour effect
   *
   * @example.files StageChangeColorEffect.java
   * @scratchblock change [color v] effect by (by)
   */
  public void changeColorEffect(double by) {
    this.setColorEffect(this.backdropEffects.colorEffect() + by);
  }

  /** @return the backdrop's colour effect */
  public double getColorEffect() {
    return this.backdropEffects.colorEffect();
  }

  /**
   * Scratch's ghost effect on the backdrop: the stage colour shows through.
   *
   * @param percent 0 opaque, 100 invisible
   *
   * @example.files StageSetTransparency.java
   * @scratchblock set [ghost v] effect to (transparency)
   */
  public void setTransparency(double percent) {
    this.backdropEffects = new Effects(this.backdropEffects.colorEffect(), Math.max(0, Math.min(100, percent)),
        this.backdropEffects.tint());
  }

  /**
   * @param by how much to change the backdrop's transparency
   *
   * @example.files StageChangeTransparency.java
   * @scratchblock change [ghost v] effect by (step)
   */
  public void changeTransparency(double by) {
    this.setTransparency(this.backdropEffects.transparency() + by);
  }

  /** @return the backdrop's transparency in percent */
  public double getTransparency() {
    return this.backdropEffects.transparency();
  }

  /**
   * Multiplies the backdrop by a colour. Not a Scratch block: Scratch's colour effect is
   * {@link #setColorEffect(double)}.
   *
   * @param color the tint, or null for none
   *
   * @example.files StageSetTint.java
   */
  public void setTint(Color color) {
    this.backdropEffects = new Effects(this.backdropEffects.colorEffect(), this.backdropEffects.transparency(),
        color == null ? 0xffffffff : color.toARGB());
  }

  /**
   * @param color the colour behind the backdrop
   *
   * @example.files StageSetColor.java
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /**
   * @param red red from 0 to 255
   * @param green green from 0 to 255
   * @param blue blue from 0 to 255
   *
   * @example.files StageSetColor.java
   */
  public void setColor(double red, double green, double blue) {
    this.color = new Color(red, green, blue);
  }

  /**
   * @return the colour behind the backdrop
   *
   * @example.files StageGetColor.java
   */
  public Color getColor() {
    return this.color;
  }

  // ---- sound ----------------------------------------------------------------------

  /**
   * Adds a sound (WAV or OGG).
   *
   * @param name the sound's name, for {@link #playSound(String)}
   * @param soundPath the sound file
   *
   * @example.files StageAddSound.java
   */
  public void addSound(String name, String soundPath) {
    this.sounds.add(name, soundPath);
  }

  /**
   * Adds a sound, named after its file.
   *
   * @param soundPath the sound file
   *
   * @example.files StageAddSound.java
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
   * @example.files StagePlaySound.java
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
   * @example.files StageStopSound.java
   */
  public void stopSound(String name) {
    this.sounds.stop(name);
  }

  /**
   * Stops every sound this object started.
   *
   * @scratchblock stop all sounds
   * @example.files StageStopAllSounds.java
   */
  public void stopAllSounds() {
    this.sounds.stopAll();
  }

  /**
   * @param name a sound's name
   * @return whether it is playing
   *
   * @example.files StageIsSoundPlaying.java
   */
  public boolean isSoundPlaying(String name) {
    return this.sounds.isPlaying(name);
  }

  /**
   * @param volume this object's volume from 0 to 100
   *
   * @scratchblock set volume to (percent) %
   * @example.files StageSetVolume.java
   */
  public void setVolume(double volume) {
    this.sounds.setVolume(volume);
  }

  /**
   * @param by how much to change the volume
   *
   * @scratchblock change volume by (step)
   * @example.files StageChangeVolume.java
   */
  public void changeVolume(double by) {
    this.sounds.setVolume(this.sounds.getVolume() + by);
  }

  /**
   * @return this object's volume from 0 to 100
   *
   * @scratchblock (volume)
   * @example.files StageGetVolume.java
   */
  public double getVolume() {
    return this.sounds.getVolume();
  }

  // ---- canvases: pens and stamps ------------------------------------------------------

  Canvas canvas(Layer layer) {
    Canvas c = this.canvases[layer.ordinal()];
    if (c == null) {
      c = new Canvas(this.width, this.height);
      this.canvases[layer.ordinal()] = c;
    }
    return c;
  }

  /** A pen line between two world points; the canvas is fixed on screen, so the camera applies now. */
  void penLine(Layer layer, double x0, double y0, double x1, double y1, double size, int argb) {
    double zoom = layer == Layer.UI ? 1 : this.camera.getZoom() / 100;
    if (layer == Layer.UI) {
      this.canvas(layer).line(x0, y0, x1, y1, size, argb);
    } else {
      this.canvas(layer).line(this.camera.toScreenX(x0), this.camera.toScreenY(y0), this.camera.toScreenX(x1),
          this.camera.toScreenY(y1), size * zoom, argb);
    }
  }

  void stamp(Layer layer, Image costume, double[] costumeToWorld, Effects effects, boolean ui) {
    double[] m = costumeToWorld;
    if (!ui && layer != Layer.UI) {
      // world → screen: scale by zoom around the camera position
      double z = this.camera.getZoom() / 100, cx = this.camera.getX(), cy = this.camera.getY();
      m = new double[] {m[0] * z, m[1] * z, m[2] * z, m[3] * z, (m[4] - cx) * z, (m[5] - cy) * z};
    }
    this.canvas(layer).stamp(costume, m, effects);
  }

  void eraseLayer(Layer layer) {
    Canvas c = this.canvases[layer.ordinal()];
    if (c != null) {
      c.eraseAll();
    }
  }

  /**
   * Erases everything every pen drew and everything stamped, on every layer.
   *
   * @scratchblock erase all
   * @example.files StageEraseAll.java
   */
  public void eraseAll() {
    for (Layer layer : Layer.values()) {
      this.eraseLayer(layer);
    }
  }

  // ---- colour sensing and pixels: one CPU compositor ------------------------------------

  /** World → screen through the camera, applied to a costume → world map. */
  double[] toScreen(double[] m) {
    double z = this.camera.getZoom() / 100, cx = this.camera.getX(), cy = this.camera.getY();
    return new double[] {m[0] * z, m[1] * z, m[2] * z, m[3] * z, (m[4] - cx) * z, (m[5] - cy) * z};
  }

  /** The world layers as they are now, back to front, without {@code left}. */
  Compositor compositor(Sprite left) {
    List<Compositor.Part> parts = new ArrayList<>();
    if (!this.backdrops.isEmpty()) {
      Image image = this.backdrops.get(this.currentBackdrop).image();
      parts.add(new Compositor.Picture(image, this.toScreen(this.backdropToWorld(this.currentBackdrop)),
          this.backdropEffects));
    }
    for (Layer layer : new Layer[] {Layer.BACKGROUND, Layer.MAIN, Layer.FOREGROUND}) {
      Canvas canvas = this.canvases[layer.ordinal()];
      if (canvas != null) {
        parts.add(new Compositor.CanvasPart(canvas));
      }
      for (Sprite s : this.sprites.get(layer.ordinal())) {
        Compositor.Picture picture = s == left ? null : s.picture(true);
        if (picture != null) {
          parts.add(picture);
        }
      }
    }
    return new Compositor(this.width, this.height, this.color.toARGB(), parts);
  }

  // ---- events --------------------------------------------------------------------------

  /**
   * Sends a message to every sprite and then the stage; their {@code whenIReceive} runs at once.
   *
   * @param message the message
   *
   * @scratchblock broadcast [message v]
   * @example.files StageBroadcast.java
   */
  public void broadcast(String message) {
    for (Sprite s : this.spritesInOrder()) {
      s.whenIReceive(message);
    }
    this.whenIReceive(message);
  }

  /**
   * Called when a message is broadcast.
   *
   * @param message the message
   *
   * @scratchblock when I receive [message v]
   * @example.files StageWhenIReceive.java
   */
  public void whenIReceive(String message) {}

  /**
   * Called when a key is pressed, once per press.
   *
   * @param key the key
   *
   * @example.files StageWhenKeyPressed.java
   * @scratchblock when [space v] key pressed
   */
  public void whenKeyPressed(KeyCode key) {}

  /**
   * Called when a key is released.
   *
   * @param key the key
   *
   * @example.files StageWhenKeyReleased.java
   */
  public void whenKeyReleased(KeyCode key) {}

  /**
   * Called when a mouse button is pressed anywhere on the stage.
   *
   * @param button the button
   *
   * @scratchblock when stage clicked
   * @example.files StageWhenMouseClicked.java
   */
  public void whenMouseClicked(MouseCode button) {}

  /**
   * Called when the mouse wheel turns.
   *
   * @param steps notches; positive is towards the user
   *
   * @example.files StageWhenMouseWheelMoved.java
   */
  public void whenMouseWheelMoved(double steps) {}

  /**
   * @param key a key
   * @return whether the key is held down
   *
   * @example.files StageIsKeyPressed.java
   * @scratchblock &lt;key [space v] pressed?&gt;
   */
  public boolean isKeyPressed(KeyCode key) {
    return Game.current().getInput().isKeyDown(key);
  }

  /**
   * @return whether a mouse button is held down
   *
   * @example.files StageIsMouseDown.java
   * @scratchblock &lt;mouse down?&gt;
   */
  public boolean isMouseDown() {
    return Game.current().getInput().isMouseDown();
  }

  /**
   * @return the mouse pointer in world coordinates
   *
   * @example.files StageGetMouse.java
   */
  public Vector2 getMouse() {
    var input = Game.current().getInput();
    return new Vector2(this.camera.toWorldX(input.getMouseX()), this.camera.toWorldY(input.getMouseY()));
  }

  /**
   * @return x of the mouse pointer in world coordinates
   *
   * @scratchblock (mouse x)
   * @example.files StageGetMouseX.java
   */
  public double getMouseX() {
    return this.getMouse().getX();
  }

  /**
   * @return y of the mouse pointer in world coordinates
   *
   * @scratchblock (mouse y)
   * @example.files StageGetMouseY.java
   */
  public double getMouseY() {
    return this.getMouse().getY();
  }

  /**
   * A random whole number between two numbers, both included.
   *
   * @param from the lowest
   * @param to the highest
   * @return the number
   *
   * @example.files StagePickRandom.java
   * @scratchblock (pick random (from) to (to))
   */
  public int pickRandom(int from, int to) {
    return Random.randomInt(from, to);
  }

  /**
   * @return this object's timer, counting game time since it was made or reset
   *
   * @scratchblock (timer)
   * @example.files StageGetTimer.java
   */
  public Timer getTimer() {
    return this.timer;
  }

  // ---- waiting ----------------------------------------------------------------------

  /**
   * Holds up the code that calls it for a number of seconds. Called in {@code main} (a program
   * written straight down the page), the game keeps running meanwhile and the seconds are game
   * time. Called in {@code run()} or an event, the whole game waits, as it does for any slow
   * code; the window stays responsive.
   *
   * @param seconds how long
   */
  public void waitSeconds(double seconds) {
    Game game = Game.current();
    if (game.isGameThread() || game.isHeadless()) {
      sleep(Math.round(seconds * 1000));
      return;
    }
    double until = game.getTime() + seconds;
    while (game.getTime() < until - 1e-9 && game.getStopped() == null) {
      sleep(1);
    }
  }

  /**
   * Holds up the code that calls it until a condition is true; see {@link #waitSeconds(double)}.
   *
   * @param condition checked about every millisecond
   *
   * @scratchblock wait until &lt;condition&gt;
   * @example.files StageWaitUntil.java
   */
  public void waitUntil(java.util.function.BooleanSupplier condition) {
    while (!condition.getAsBoolean()) {
      sleep(1);
    }
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(Math.max(0, millis));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  // ---- display and ask ------------------------------------------------------------------

  /**
   * Shows a text in a band along the bottom of the stage until something else is displayed; an
   * empty text removes the band.
   *
   * @param text what to show
   *
   * @example.files StageDisplay.java
   */
  public void display(String text) {
    this.displayBand.showText(text);
  }

  /**
   * Shows a text in a band along the bottom of the stage for a number of seconds of game time.
   *
   * @param text what to show
   * @param seconds how long
   *
   * @example.files StageDisplay.java
   */
  public void display(String text, double seconds) {
    this.displayBand.showText(text, seconds);
  }

  /**
   * Asks a question in a box along the bottom of the stage, as Scratch does, with a field to type
   * the answer in. Enter or the check button finishes it: then {@link #isAsking()} is false and {@link #getAnswer()} has the answer.
   * While a question is open, keys go to the answer box, not to the stage and its sprites.
   *
   * @param question the question
   *
   * @scratchblock ask [question] and wait
   * @example.files StageAsk.java
   */
  public void ask(String question) {
    this.askingSprite = null;
    this.startAsking(question);
  }

  void askForSprite(Sprite sprite, String question) {
    this.askingSprite = sprite;
    this.startAsking(question);
  }

  private void startAsking(String question) {
    this.question = question;
    this.typed.setLength(0);
  }

  /**
   * @return whether a question is waiting for its answer
   *
   * @example.files StageIsAsking.java
   */
  public boolean isAsking() {
    return this.question != null;
  }

  /**
   * @return the answer to the last question, "" before the first
   *
   * @scratchblock (answer)
   * @example.files StageGetAnswer.java
   */
  public String getAnswer() {
    return this.answer;
  }

  /** Keys and characters while a question is open. */
  private void typeIntoAnswer(InputEvent event) {
    if (event instanceof InputEvent.Typed t) {
      this.typed.appendCodePoint(t.codepoint());
    } else if (event instanceof InputEvent.Repeat r) {
      if (r.key() == KeyCode.BACKSPACE && this.typed.length() > 0) {
        this.typed.setLength(this.typed.offsetByCodePoints(this.typed.length(), -1));
      }
    } else if (event instanceof InputEvent.Key k && k.pressed()) {
      if (k.key() == KeyCode.BACKSPACE && this.typed.length() > 0) {
        this.typed.setLength(this.typed.offsetByCodePoints(this.typed.length(), -1));
      } else if (k.key() == KeyCode.ENTER) {
        this.submitAnswer();
      }
    }
  }

  private void submitAnswer() {
    this.answer = this.typed.toString();
    this.question = null;
    this.typed.setLength(0);
    if (this.askingSprite != null) {
      this.askingSprite.say(""); // the question's bubble goes with the question
      this.askingSprite = null;
    }
  }

  /**
   * Runs every tick before the sprites' {@code run()}. Override it with the stage's code.
   *
   * @example.files StageRun.java
   */
  public void run() {}

  // ---- the tick and the draw list (through SceneBridge) ------------------------------------

  void tick() {
    if (this.sorting != null) {
      for (List<Sprite> layer : this.sprites) {
        layer.sort(this.sorting);
      }
    }
    List<Sprite> order = this.spritesInOrder();
    for (Sprite s : order) {
      if (s.getStage() == this) {
        s.advanceGlide();
      }
    }
    this.run();
    for (Sprite s : order) {
      // a sprite removed earlier in this tick does not run any more
      if (s.getStage() == this) {
        s.run();
      }
    }
    // timed texts and bubbles count game time
    for (Text t : List.copyOf(this.texts)) {
      t.tick();
    }
    for (Sprite s : this.spritesInOrder()) {
      s.getText().tick();
    }
    this.displayBand.tick();
  }

  void dispatch(InputEvent event) {
    if (this.question != null && (event instanceof InputEvent.Key || event instanceof InputEvent.Typed
        || event instanceof InputEvent.Repeat)) {
      this.typeIntoAnswer(event);
      return;
    }
    if (this.question != null && this.askBox != null && event instanceof InputEvent.Button b && b.pressed()
        && this.askBox.onButton(b.x(), b.y())) {
      this.submitAnswer(); // the check button, as in Scratch
      return;
    }
    if (event instanceof InputEvent.Key k) {
      if (k.pressed()) {
        this.whenKeyPressed(k.key());
        for (Sprite s : this.spritesInOrder()) {
          s.whenKeyPressed(k.key());
        }
      } else {
        this.whenKeyReleased(k.key());
        for (Sprite s : this.spritesInOrder()) {
          s.whenKeyReleased(k.key());
        }
      }
    } else if (event instanceof InputEvent.Button b && b.pressed()) {
      this.whenMouseClicked(b.button());
      List<Sprite> order = this.spritesInOrder();
      for (Sprite s : order) {
        s.whenMouseClicked(b.button());
      }
      // only the frontmost sprite under the pointer is clicked (decision log, Phase 2)
      for (int i = order.size() - 1; i >= 0; i--) {
        Sprite s = order.get(i);
        if (s.getStage() == this && s.isTouchingMousePointer()) {
          s.whenClicked();
          break;
        }
      }
    } else if (event instanceof InputEvent.Move) {
      Vector2 m = this.getMouse();
      for (Sprite s : this.spritesInOrder()) {
        Vector2 at = s.getMouse();
        s.whenMouseMoved(at.getX(), at.getY());
      }
    } else if (event instanceof InputEvent.Wheel w) {
      this.whenMouseWheelMoved(w.steps());
    }
  }

  DrawList draw(long tick) {
    List<LayerDraw> layers = new ArrayList<>();
    Game current = Game.currentOrNull();
    boolean debug = current != null && current.isDebug();
    for (Layer layer : Layer.values()) {
      // extensions' drawers first: after the layer's canvas, before its sprites
      List<Item> items = new ArrayList<>(current == null ? List.of() : current.getExtensions().drawLayer(layer, this));
      for (Sprite s : this.sprites.get(layer.ordinal())) {
        SpriteDraw d = s.toDraw();
        if (d != null) {
          items.add(d);
        }
      }
      if (debug) {
        // hitboxes in front of the layer's sprites: what touching uses
        for (Sprite s : this.sprites.get(layer.ordinal())) {
          Shape hitbox = s.touchHitbox();
          if (hitbox != null) {
            items.add(new ShapeDraw(hitbox.getPoints(), 0, DEBUG_COLOR, 2));
          }
        }
      }
      Canvas canvas = this.canvases[layer.ordinal()];
      layers.add(new LayerDraw(layer.ordinal(), canvas != null, canvas == null ? List.of() : canvas.takeChanges(),
          items, layer != Layer.UI));
    }
    SpriteDraw backdrop = null;
    if (!this.backdrops.isEmpty()) {
      Image image = this.backdrops.get(this.currentBackdrop).image();
      double[] m = this.backdropToWorld(this.currentBackdrop);
      backdrop = new SpriteDraw(image, 0, 0, image.getWidth() / 2.0, image.getHeight() / 2.0, m[0], -m[3], 0,
          this.backdropEffects, null, 1, 0, 0, List.of());
    }
    // texts of the world and bubbles go into the sharp overlay, UI texts in front of the UI layer
    List<TextDraw> overlay = new ArrayList<>(), ui = new ArrayList<>();
    for (Text t : this.texts) {
      TextDraw d = t.toDraw();
      if (d != null) {
        (t.getLayer() == Layer.UI ? ui : overlay).add(d);
      }
    }
    for (Sprite s : this.spritesInOrder()) {
      TextDraw d = s.getText().toDraw();
      if (d != null) {
        (s.getLayer() == Layer.UI ? ui : overlay).add(d);
      }
    }
    // at the bottom: the ask box, and the display band above it
    double bottom = -this.height / 2.0 + AskDraw.MARGIN;
    this.askBox = this.question == null ? null : this.askBox(bottom, tick);
    if (this.askBox != null) {
      bottom += this.askBox.height() + 4;
    }
    if (this.displayBand.isVisible()) {
      this.displayBand.setPosition(-this.width / 2.0 + 8, bottom);
      ui.add(this.displayBand.toDraw());
    }
    if (debug) {
      this.debugTexts(overlay, ui, layers.get(Layer.UI.ordinal()).items());
    }
    return new DrawList(this.id, tick, this.width, this.height, this.color.toARGB(), backdrop,
        new CameraDraw(this.camera.getX(), this.camera.getY(), this.camera.getZoom()), layers,
        List.copyOf(overlay), List.copyOf(ui), this.askBox, debug, this.shaders.toDraw(),
        this.monitorDraws(), null, current == null ? List.of() : current.getExtensions().drawOverlay(this),
        this.cursor == null ? null : new org.openpatch.scratch.internal.draw.CursorDraw(Images.load(this.cursor),
            this.cursorX, this.cursorY));
  }

  /**
   * Scratch's ask box standing on {@code bottom}: the question when the stage asks (a sprite's
   * question is in its bubble), then the answer field, which shows the end of a long answer.
   */
  private AskDraw askBox(double bottom, long tick) {
    double width = this.width - 2 * AskDraw.MARGIN;
    TextLayout question = this.askingSprite == null && !this.question.isEmpty()
        ? this.displayBand.layoutOf(this.question, width - 2 * AskDraw.PADDING) : null;
    double room = width - 2 * AskDraw.PADDING - AskDraw.INSET - AskDraw.BUTTON - 2 * AskDraw.BUTTON_INSET;
    String shown = this.typed.toString();
    TextLayout answer = this.displayBand.layoutOf(shown, 0);
    while (answer.width() > room && !shown.isEmpty()) {
      shown = shown.substring(shown.offsetByCodePoints(0, 1));
      answer = this.displayBand.layoutOf(shown, 0);
    }
    double height = AskDraw.heightFor(question == null ? 0 : question.height());
    boolean caret = tick / 30 % 2 == 0; // blinks every half second
    return new AskDraw(question, answer, -this.width / 2.0 + AskDraw.MARGIN, bottom + height, width, height, caret);
  }

  /** The colour of everything debug mode draws, as in 6.x. */
  private static final int DEBUG_COLOR = 0xffff0000;

  /**
   * What debug mode shows besides the hitboxes, as 6.x did: each sprite's direction above it and
   * its position on it, a crosshair at the mouse with its world coordinates, and the camera under
   * the frame rate.
   */
  private void debugTexts(List<TextDraw> overlay, List<TextDraw> ui, List<Item> uiItems) {
    for (Sprite s : this.spritesInOrder()) {
      if (!s.isVisible()) {
        continue;
      }
      List<TextDraw> into = s.getLayer() == Layer.UI ? ui : overlay;
      into.add(debugText("Direction: " + round2(s.getDirection()), s.getX(), s.getY() + s.getHeight() / 2 + 10, 1));
      into.add(debugText("(" + round2(s.getX()) + ", " + round2(s.getY()) + ")", s.getX(), s.getY(), 1));
    }
    Game game = Game.currentOrNull();
    if (game != null) {
      double mx = game.getInput().getMouseX(), my = game.getInput().getMouseY();
      double w = this.width / 2.0, h = this.height / 2.0;
      uiItems.add(new ShapeDraw(new double[] {mx, -h, mx, h}, 0, DEBUG_COLOR, 1));
      uiItems.add(new ShapeDraw(new double[] {-w, my, w, my}, 0, DEBUG_COLOR, 1));
      ui.add(debugText("(" + round2(this.camera.toWorldX(mx)) + ", " + round2(this.camera.toWorldY(my)) + ")",
          mx + 4, my + 8, 0));
    }
    ui.add(debugText("Camera: (" + round2(this.camera.getX()) + ", " + round2(this.camera.getY()) + ") "
        + round2(this.camera.getZoom()) + "%", this.width / 2.0 - 6, this.height / 2.0 - 24, 2));
  }

  /** A line of debug text centred vertically on y; {@code align} 0 starts, 1 centres, 2 ends at x. */
  private static TextDraw debugText(String text, double x, double y, int align) {
    var layout = org.openpatch.scratch.internal.text.TextLayout.of(text,
        org.openpatch.scratch.internal.text.Font.defaultFont(), 12, 0);
    double left = align == 1 ? x - layout.width() / 2 : align == 2 ? x - layout.width() : x;
    var font = layout.font();
    double top = y + (font.ascent(12) + font.descent(12)) / 2;
    return new TextDraw(layout, left, top, layout.width(), layout.height(), 0, DEBUG_COLOR, TextDraw.Box.NONE, 0, 0, 0,
        0);
  }

  private static double round2(double value) {
    return Math.round(value * 100) / 100.0;
  }

  /** Stage monitors first, then the sprites', stacked from the top left. */
  private List<org.openpatch.scratch.internal.draw.MonitorDraw> monitorDraws() {
    List<Monitor> all = new ArrayList<>(this.monitors.values());
    for (Sprite s : this.spritesInOrder()) {
      all.addAll(s.monitors());
    }
    List<org.openpatch.scratch.internal.draw.MonitorDraw> out = new ArrayList<>();
    var font = org.openpatch.scratch.internal.text.Font.defaultFont();
    double y = this.height / 2.0 - 6;
    for (Monitor m : all) {
      out.add(m.toDraw(font, -this.width / 2.0 + 6, y));
      y -= org.openpatch.scratch.internal.draw.MonitorDraw.STEP;
    }
    return List.copyOf(out);
  }

  /** The stage's side of {@link Game.Scene}: plumbing that stays out of the public API. */
  private static final class SceneBridge implements Game.Scene {

    @Override
    public void tick(Stage stage) {
      stage.tick();
    }

    @Override
    public void dispatch(Stage stage, InputEvent event) {
      stage.dispatch(event);
    }

    @Override
    public DrawList draw(Stage stage, long tick) {
      return stage.draw(tick);
    }

    @Override
    public void shown(Stage stage, boolean shown) {
      if (shown) {
        // the renderer may have let go of this stage's canvas textures meanwhile
        for (Canvas c : stage.canvases) {
          if (c != null) {
            c.resendAll();
          }
        }
      }
    }

    @Override
    public void exit(Stage stage) {}

    @Override
    public List<Sprite> sprites(Stage stage) {
      return stage.spritesInOrder();
    }

    @Override
    public int id(Stage stage) {
      return stage.id;
    }
  }
}
