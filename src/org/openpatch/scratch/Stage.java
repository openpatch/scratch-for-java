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
import org.openpatch.scratch.extensions.hitbox.Hitbox;
import org.openpatch.scratch.extensions.pen.Pen;
import org.openpatch.scratch.extensions.text.Text;
import org.openpatch.scratch.extensions.text.TextStyle;
import org.openpatch.scratch.extensions.timer.Timer;
import org.openpatch.scratch.internal.Applet;
import org.openpatch.scratch.internal.Color;
import org.openpatch.scratch.internal.Font;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Sound;
import org.openpatch.scratch.internal.Stamp;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/** Represents a scratch stage. */
public class Stage {

  private final List<Image> backdrops = new CopyOnWriteArrayList<>();
  private Color color = new Color();
  private int currentBackdrop = 0;
  private final List<Sound> sounds = new CopyOnWriteArrayList<>();

  public Queue<Stamp> backgroundStamps;
  private PGraphics backgroundBuffer;
  private boolean eraseBackgroundBuffer;

  public Queue<Stamp> foregroundStamps;
  private PGraphics foregroundBuffer;
  private boolean eraseForegroundBuffer;

  private PGraphics debugBuffer;

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

  public Stage() {
    this(480, 360);
  }

  public Stage(int width, final int height) {
    this(width, height, null);
  }

  public Stage(int width, final int height, String assets) {
    this.texts = new CopyOnWriteArrayList<>();
    this.pens = new CopyOnWriteArrayList<>();
    this.sprites = new CopyOnWriteArrayList<>();
    this.backgroundStamps = new ConcurrentLinkedQueue<>();
    this.foregroundStamps = new ConcurrentLinkedQueue<>();
    this.timer = new ConcurrentHashMap<>();
    if (Window.getInstance() == null) {
      new Window(width, height, assets);
      Applet a = Applet.getInstance();
      a.setStage(this);
    }
    Applet applet = Applet.getInstance();
    this.backgroundBuffer =
        applet.createGraphics(applet.width, applet.height, applet.sketchRenderer());
    this.foregroundBuffer =
        applet.createGraphics(applet.width, applet.height, applet.sketchRenderer());
    this.debugBuffer = applet.createGraphics(applet.width, applet.height, applet.sketchRenderer());
    /**
     * Smooth does currently not work on Apple Silicon
     * https://github.com/processing/processing4/issues/694
     */
    this.backgroundBuffer.smooth(4);
    this.foregroundBuffer.smooth(4);
    this.debugBuffer.smooth(4);
    this.timer.put("default", new Timer());
    this.display =
        new Text(null, -applet.width / 2, -applet.height / 2, applet.width, TextStyle.BOX);
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
    p.addPoint(-this.getWidth() / 2, this.getHeight() / 2);
    p.addPoint(-this.getWidth() / 2, this.getHeight() / 2 + 5);
    p.addPoint(this.getWidth() / 2, this.getHeight() / 2 + 5);
    p.addPoint(this.getWidth() / 2, this.getHeight() / 2);
    this.topBorder = new Hitbox(p);

    p = new Polygon();
    p.addPoint(-this.getWidth() / 2, -this.getHeight() / 2);
    p.addPoint(-this.getWidth() / 2, -this.getHeight() / 2 - 5);
    p.addPoint(this.getWidth() / 2, -this.getHeight() / 2 - 5);
    p.addPoint(this.getWidth() / 2, -this.getHeight() / 2);
    this.bottomBorder = new Hitbox(p);
  }

  public void setDebug(boolean debug) {
    Applet.getInstance().setDebug(debug);
  }

  public boolean isDebug() {
    return Applet.getInstance().isDebug();
  }

  /**
   * Add a sprite, text, pen or image to the stage.
   *
   * @param drawable
   */
  public void add(Sprite sprite) {
    this.sprites.add(sprite);
    sprite.addedToStage(this);
  }

  public void add(Text text) {
    this.texts.add(text);
    text.addedToStage(this);
  }

  public void add(Pen pen) {
    this.pens.add(pen);
    pen.addedToStage(this);
  }

  public void goLayersBackwards(Sprite sprite, int number) {
    int index = this.sprites.indexOf(sprite);
    if (index == -1) return;
    int newIndex = index - number;
    if (newIndex < 0) newIndex = 0;
    newIndex = Math.min(newIndex, this.sprites.size() - 1);
    this.sprites.remove(index);
    this.sprites.add(newIndex, sprite);
  }

  public void goLayersForwards(Sprite sprite, int number) {
    int index = this.sprites.indexOf(sprite);
    if (index == -1) return;
    int newIndex = index + number;
    if (newIndex < 0) newIndex = 0;
    newIndex = Math.min(newIndex, this.sprites.size() - 1);
    this.sprites.remove(index);
    this.sprites.add(newIndex, sprite);
  }

  public void goToFrontLayer(Sprite sprite) {
    this.sprites.remove(sprite);
    this.sprites.add(sprite);
  }

  public void goToBackLayer(Sprite sprite) {
    this.sprites.remove(sprite);
    this.sprites.add(0, sprite);
  }

  public void setSorter(Comparator<? super Sprite> sorter) {
    this.sorter = sorter;
  }

  public List<Sprite> getAll() {
    return new CopyOnWriteArrayList<>(this.sprites);
  }

  /**
   * Rise a sprite, text, pen or image.
   *
   * @param drawable
   */
  public void remove(Sprite sprite) {
    this.sprites.remove(sprite);
    sprite.removedFromStage(this);
  }

  public void remove(Pen pen) {
    this.pens.remove(pen);
    pen.removedFromStage(this);
  }

  public void remove(Text text) {
    this.texts.remove(text);
    text.removedFromStage(this);
  }

  public void removeAll() {
    this.removeAllSprites();
    this.removeAllTexts();
    this.removeAllPens();
  }

  public void removeAllSprites() {
    for (Sprite sprite : this.sprites) {
      sprite.removedFromStage(this);
    }
    this.sprites.clear();
  }

  public void removeAllTexts() {
    for (Text text : this.texts) {
      text.removedFromStage(this);
    }
    this.texts.clear();
  }

  public void removeAllPens() {
    for (Pen pen : this.pens) {
      pen.removedFromStage(this);
    }
    this.pens.clear();
  }

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

  public <T extends Sprite> List<T> findSpritesOf(Class<T> c) {
    return this.find(c);
  }

  public <T extends Text> List<T> findTextsOf(Class<T> c) {
    return this.texts.stream().filter(c::isInstance).map(c::cast).collect(Collectors.toList());
  }

  public <T extends Pen> List<T> findPensOf(Class<T> c) {
    return this.pens.stream().filter(c::isInstance).map(c::cast).collect(Collectors.toList());
  }

  public <T extends Sprite> long count(Class<T> c) {
    return this.sprites.stream().filter(c::isInstance).count();
  }

  public long countSprites() {
    return this.sprites.size();
  }

  public <T extends Sprite> long countSpritesOf(Class<T> c) {
    return this.count(c);
  }

  public long countTexts() {
    return this.texts.size();
  }

  public <T extends Text> long countTextsOf(Class<T> c) {
    return this.texts.stream().filter(c::isInstance).count();
  }

  public long countPens() {
    return this.pens.size();
  }

  public <T extends Pen> long countPensOf(Class<T> c) {
    return this.pens.stream().filter(c::isInstance).count();
  }

  /**
   * Add a backdrop to the stage. If a backdrop with the received name already exists do nothing.
   *
   * @param name a unique name
   * @param imagePath a image path
   * @param stretch stretch image to window size
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
   * Add a backdrop to the stage. If a backdrop with the received name already exists do nothing.
   *
   * @param name a unique name
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

  public void whenBackdropSwitches(String name) {}

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
  }

  public void eraseBackground() {
    this.eraseBackgroundBuffer = true;
  }

  public void eraseForeground() {
    this.eraseForegroundBuffer = true;
  }

  /**
   * Add a sound to the stage. If a sound with the received name already exists do nothing.
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
   * Returns the pen buffer
   *
   * @return the pen buffer
   */
  public PGraphics getBackgroundBuffer() {
    return this.backgroundBuffer;
  }

  public PGraphics getForegroundBuffer() {
    return this.foregroundBuffer;
  }

  public PGraphics getDebugBuffer() {
    return this.debugBuffer;
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
   */
  public void setTint(double r, double g, double b) {
    if (this.backdrops.size() == 0) return;
    this.backdrops.get(this.currentBackdrop).setTint(r, g, b);
  }

  /**
   * Sets the tint for the current backdrop with a hue.
   *
   * @see Image#setTint(double)
   */
  public void setTint(double h) {
    if (this.backdrops.size() == 0) return;
    this.backdrops.get(this.currentBackdrop).setTint(h);
  }

  /**
   * Changes the tint for the current backdrop
   *
   * @see Image#changeTint(double)
   */
  public void changeTint(double step) {
    if (this.backdrops.size() == 0) return;

    this.backdrops.get(this.currentBackdrop).changeTint(step);
  }

  /**
   * Sets the transparency of the current backdrop.
   *
   * @see Image#setTransparency(double)
   */
  public void setTransparency(double transparency) {
    this.backdrops.get(this.currentBackdrop).setTransparency(transparency);
  }

  /**
   * Changes the transparency for the current costume.
   *
   * @see Image#changeTransparency(double)
   */
  public void changeTransparency(double step) {
    if (this.backdrops.size() == 0) return;

    this.backdrops.get(this.currentBackdrop).changeTransparency(step);
  }

  /**
   * Return the width of the current costume or the pen size, when no costume is available.
   *
   * @return the width of the sprite
   */
  public int getWidth() {
    return Applet.getInstance().getWidth();
  }

  /**
   * Return the height of the current costume or the pen size, when no costume is available.
   *
   * @return the height of the sprite
   */
  public int getHeight() {
    return Applet.getInstance().getHeight();
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

  public void mouseEvent(MouseEvent e) {
    this.mouseX = e.getX() - this.getWidth() / 2;
    this.mouseY = -(e.getY() - this.getHeight() / 2);
    this.mouseDown = false;

    if (e.getAction() == MouseEvent.PRESS) {
      this.mouseDown = true;
    } else if (e.getAction() == MouseEvent.CLICK) {
      if (e.getButton() == PConstants.LEFT) {
        whenMouseClicked(MouseCode.LEFT);
      } else if (e.getButton() == PConstants.RIGHT) {
        whenMouseClicked(MouseCode.RIGHT);
      } else if (e.getButton() == PConstants.CENTER) {
        whenMouseClicked(MouseCode.CENTER);
      }
      this.sprites.stream()
          .forEach(
              s -> {
                s.mouseEvent(e);
                if (s.isTouchingMousePointer()) {
                  s.whenClicked();
                }
              });
    } else if (e.getAction() == MouseEvent.MOVE) {
      this.sprites.stream().forEach(s -> s.whenMouseMoved(this.mouseX, this.mouseY));
    } else if (e.getAction() == MouseEvent.WHEEL) {
      this.whenMouseWheelMoved(e.getCount());
    }
  }

  public void whenMouseClicked(MouseCode mouseEvent) {}

  public void whenMouseWheelMoved(int steps) {}

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
   * Returns true is the mouse button is down
   *
   * @return mouse button down
   */
  public boolean isMouseDown() {
    return this.mouseDown;
  }

  public void whenKeyPressed(int keyCode) {}

  public void keyEvent(KeyEvent e) {
    switch (e.getAction()) {
      case KeyEvent.PRESS:
        this.whenKeyPressed(e.getKeyCode());
        this.keyCodePressed.put(e.getKeyCode(), true);
        break;
      case KeyEvent.RELEASE:
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

  public int pickRandom(int from, final int to) {
    if (to < from) {
      return to + (int) (Math.random() * (from - to));
    }
    return from + (int) (Math.random() * (to - from));
  }

  public void display(String text) {
    this.display.showText(text);
  }

  public void display(String text, final int millis) {
    this.display.showText(text, millis);
  }

  public void broadcast(String message) {
    this.sprites.stream().forEach(s -> s.whenIReceive(message));
  }

  public void whenIReceive(String message) {}

  /** Draws the current backdrop or if none a solid color */
  public void pre() {
    Applet applet = Applet.getInstance();
    if (applet == null) return;
    // redraw background to clear screen
    applet.background(
        (float) this.color.getRed(), (float) this.color.getGreen(), (float) this.color.getBlue());

    // draw current backdrop
    if (this.backdrops.size() > 0) {
      this.backdrops.get(this.currentBackdrop).drawAsBackground();
    }
    this.backgroundBuffer.beginDraw();
    if (this.eraseBackgroundBuffer) {
      this.backgroundBuffer.clear();
      this.eraseBackgroundBuffer = false;
    }
    this.pens.stream().filter(p -> p.isInBackground()).forEach(p -> p.draw());
    this.sprites.stream().forEach(s -> s.getPen().draw());
    while (!this.backgroundStamps.isEmpty()) {
      this.backgroundStamps.poll().draw(this.backgroundBuffer);
    }
    this.backgroundBuffer.endDraw();
    if (this.backgroundBuffer.pixels != null) {
      applet.image(this.backgroundBuffer, applet.width / 2, applet.height / 2);
    } else {
      try {
        this.backgroundBuffer.loadPixels();
      } catch (Exception e) {
      }
    }

    this.foregroundBuffer.beginDraw();
    if (this.eraseForegroundBuffer) {
      this.foregroundBuffer.clear();
      this.eraseForegroundBuffer = false;
    }
    this.pens.stream().filter(p -> !p.isInBackground()).forEach(p -> p.draw());
    while (!this.foregroundStamps.isEmpty()) {
      this.foregroundStamps.poll().draw(this.foregroundBuffer);
    }
    this.foregroundBuffer.endDraw();
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

  public void run() {}

  /** Close the window and therefore the whole application. */
  public void exit() {
    Window.getInstance().exit();
  }

  public void draw() {
    Applet applet = Applet.getInstance();
    if (applet == null) return;

    if (this.sorter != null) {
      this.sprites.sort(this.sorter);
    }

    this.run();
    this.sprites.stream().forEach(s -> s.run());

    this.sprites.stream().forEach(s -> s.draw());
    this.texts.stream().forEach(t -> t.draw());

    if (this.display != null) {
      this.display.draw();
    }

    if (this.foregroundBuffer.pixels != null) {
      applet.image(this.foregroundBuffer, applet.width / 2, applet.height / 2);
    } else {
      try {
        this.foregroundBuffer.loadPixels();
      } catch (Exception e) {
      }
    }

    if (applet.isDebug()) {
      this.debugBuffer.beginDraw();
      this.debugBuffer.clear();
      this.sprites.stream().forEach(s -> s.drawDebug());
      this.debugBuffer.strokeWeight(1);
      this.debugBuffer.stroke(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
      this.debugBuffer.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
      var w = this.getWidth() / 2;
      var h = this.getHeight() / 2;
      this.debugBuffer.line((float) (this.mouseX + w), 0, (float) (this.mouseX + w), applet.height);
      this.debugBuffer.line(
          0, (float) (-this.mouseY + h), applet.width, (float) (-this.mouseY + h));
      this.debugBuffer.textFont(Font.getDefaultFont());
      this.debugBuffer.text(
          "(" + this.mouseX + ", " + this.mouseY + ")",
          (float) (this.mouseX + w),
          (float) (-this.mouseY + h));
      this.debugBuffer.text("FPS: " + Math.round(applet.frameRate * 100) / 100, 20, 10);
      this.debugBuffer.endDraw();

      applet.image(this.debugBuffer, applet.width / 2, applet.height / 2);
    }
  }
}
