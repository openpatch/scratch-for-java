package org.openpatch.scratch;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.Collections;

/**
 * Represents the scratch stage. Only one object of this class can be created.
 */
public class Stage {

    public static PApplet parent;
    private boolean debug;
    public static final int[] DEBUG_COLOR = { 255, 0, 0 };
    private static Stage instance;
    private CopyOnWriteArrayList<Image> backdrops = new CopyOnWriteArrayList<>();
    private Color color = new Color();
    private int currentBackdrop = 0;
    private CopyOnWriteArrayList<Sound> sounds = new CopyOnWriteArrayList<>();
    private PGraphics penBuffer;
    private Text display;
    private ConcurrentHashMap<String, Timer> timer;
    private CopyOnWriteArrayList<Drawable> drawables;
    private float mouseX;
    private float mouseY;
    private boolean mouseDown;
    private ConcurrentHashMap<Integer, Boolean> keyCodePressed = new ConcurrentHashMap<>();

    public Stage() {
        this(420, 360, false);
    }

    public Stage(int width, int height) {
        this(width, height, false);
    }

    public Stage(int width, int height, boolean debug) {
        Applet sa = new Applet(width, height);
        Stage.parent = sa;
        sa.runSketch();

        // wait to make sure the PApplet runs.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Stage.parent.imageMode(PConstants.CENTER);
        Stage.parent.rectMode(PConstants.CENTER);
        this.penBuffer = parent.createGraphics(parent.width, parent.height, parent.sketchRenderer());
        this.timer = new ConcurrentHashMap<>();
        this.timer.put("default", new Timer());
        this.display = new Text(null, 0, parent.height, true, TextStyle.BOX);
        this.debug = debug;
        this.drawables = new CopyOnWriteArrayList<>();
        if (Stage.instance != null) {
            throw new RuntimeException("You can only create one Stage object.");
        }
        Stage.instance = this;
        parent.registerMethod("pre", Stage.instance);
        parent.registerMethod("draw", Stage.instance);
        parent.registerMethod("mouseEvent", Stage.instance);
        parent.registerMethod("keyEvent", Stage.instance);
    }

    public Stage(PApplet parent) {
        this(parent, false);
    }

    public Stage(PApplet parent, boolean debug) {
        if (Stage.instance != null) {
            throw new RuntimeException("You can only create one Stage object.");
        }
        Stage.instance = this;
        Stage.parent = parent;
        Stage.parent.imageMode(PConstants.CENTER);
        Stage.parent.rectMode(PConstants.CENTER);
        this.penBuffer = parent.createGraphics(parent.width, parent.height, parent.sketchRenderer());
        this.timer = new ConcurrentHashMap<>();
        this.timer.put("default", new Timer());
        this.display = new Text(null, 0, parent.height, true, TextStyle.BOX);
        this.debug = debug;
        this.drawables = new CopyOnWriteArrayList<>();

        parent.registerMethod("pre", Stage.instance);
        parent.registerMethod("draw", Stage.instance);
        parent.registerMethod("mouseEvent", Stage.instance);
        parent.registerMethod("keyEvent", Stage.instance);
    }

    /**
     * Returns the only instance of Stage
     *
     * @return the Stage object
     */
    public static Stage getInstance() {
        return Stage.instance;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setSize(int width, int height) {
        Stage.parent.getSurface().setSize(width, height);
    }

    /**
     * Add a sprite, text, pen or image to the stage.
     *
     * @param drawable
     */
    public void add(Drawable drawable) {
        drawables.add(drawable);
    }

    /**
     * Lower a sprite, text, pen or image.
     *
     * @param drawable
     */
    public void lower(Drawable drawable) {
        int index = drawables.indexOf(drawable);
        if (index > 0) {
            Collections.swap(drawables, index, index - 1);
        }
    }

    /**
     * Rise a sprite, text, pen or image.
     *
     * @param drawable
     */
    public void raise(Drawable drawable) {
        int index = drawables.indexOf(drawable);
        if (index > -1 && index < drawables.size() - 1) {
            Collections.swap(drawables, index + 1, index);
        }
    }

    public List<Drawable> getAll() {
        return new ArrayList<>(this.drawables);
    }

    /**
     * Rise a sprite, text, pen or image.
     *
     * @param drawable
     */
    public void remove(Drawable drawable) {
        drawables.remove(drawable);
    }

    public void removeAll() {
        drawables.clear();
    }

    public void remove(Class<? extends Drawable> c) {
        drawables.removeIf(c::isInstance);
    }

    /**
     * Find sprites of a given class.
     *
     * @param c Class
     */
    public List<Drawable> find(Class<? extends Drawable> c) {
        ArrayList<Drawable> drawables = new ArrayList<>();
        for (Drawable d : this.drawables) {
            if (c.isInstance(d)) {
                drawables.add(d);
            }
        }
        return drawables;
    }

    /**
     * Add a backdrop to the stage. If a backdrop with the received name already
     * exists do nothing.
     *
     * @param name      a unique name
     * @param imagePath a image path
     */
    public void addBackdrop(String name, String imagePath) {
        for (Image backdrop : this.backdrops) {
            if (backdrop.getName().equals(name)) {
                return;
            }
        }
        this.backdrops.add(new Image(name, imagePath));
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
        for (int i = 0; i < backdrops.size(); i++) {
            Image backdrop = backdrops.get(i);
            if (backdrop.getName().equals(name)) {
                this.currentBackdrop = i;

                drawables.stream().forEach(d -> {
                    if (d instanceof Sprite) {
                        Sprite s = (Sprite) d;
                        s.whenBackdropSwitches(name);
                    }
                });
                return;
            }
        }
    }

    /**
     * Switch to the next backdrop.
     */
    public void nextBackdrop() {
        this.currentBackdrop = (this.currentBackdrop + 1) % backdrops.size();
    }

    /**
     * Switch to the previous backdrop.
     */
    public void previousBackdrop() {
        this.currentBackdrop = (this.currentBackdrop - 1) % backdrops.size();
    }

    /**
     * Switch to a random backdrop.
     */
    public void randomBackdrop() {
        int size = this.backdrops.size();
        this.currentBackdrop = this.pickRandom(0, size - 1) % size;
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

    /**
     * Erases all lines on the pen layer.
     */
    public void eraseAll() {
        this.penBuffer = parent.createGraphics(parent.width, parent.height, parent.sketchRenderer());
        this.pre();
    }

    /**
     * Add a sound to the stage. If a sound with the received name already exists do
     * nothing.
     *
     * @param name      a unique name
     * @param soundPath a sound path
     */
    public void addSound(String name, String soundPath) {
        for (Sound sound : this.sounds) {
            if (sound.getName().equals(name)) {
                return;
            }
        }

        this.sounds.add(new Sound(name, soundPath));
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
        for (Sound sound : sounds) {
            if (sound.getName().equals(name) && !sound.isPlaying()) {
                sound.play();
            }
        }
    }

    /**
     * Stops the playing of all sounds of the stage.
     */
    public void stopAllSounds() {
        for (Sound sound : sounds) {
            sound.stop();
        }
    }

    /**
     * Returns the pen buffer
     *
     * @return the pen buffer
     */
    public PGraphics getPenBuffer() {
        return this.penBuffer;
    }

    /**
     * Sets the background color via a hue value
     *
     * @param h a hue value [0...255]
     */
    public void setColor(float h) {
        this.color.setHSB(h);
    }

    /**
     * Sets the background color via a rgb value
     *
     * @param r a red value [0...255]
     * @param g a green value [0...255]
     * @param b a blue value [0...255]
     */
    public void setColor(float r, float g, float b) {
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
    public void changeColor(float h) {
        this.color.changeColor(h);
    }

    /**
     * Sets the tint for the current backdrop with rgb.
     *
     * @see Image#setTint(float, float, float)
     */
    public void setTint(int r, int g, int b) {
        if (this.backdrops.size() == 0)
            return;
        this.backdrops.get(this.currentBackdrop).setTint(r, g, b);
    }

    /**
     * Sets the tint for the current backdrop with a hue.
     *
     * @see Image#setTint(float)
     */
    public void setTint(float h) {
        if (this.backdrops.size() == 0)
            return;
        this.backdrops.get(this.currentBackdrop).setTint(h);
    }

    /**
     * Changes the tint for the current backdrop
     *
     * @see Image#changeTint(float)
     */
    public void changeTint(float step) {
        if (this.backdrops.size() == 0)
            return;

        this.backdrops.get(this.currentBackdrop).changeTint(step);
    }

    /**
     * Sets the transparency of the current backdrop.
     *
     * @see Image#setTransparency(float)
     */
    public void setTransparency(float transparency) {
        this.backdrops.get(this.currentBackdrop).setTransparency(transparency);
    }
    public void setTransparency(double transparency) {
        this.setTransparency((float) transparency);
    }

    /**
     * Changes the transparency for the current costume.
     *
     * @see Image#changeTransparency(float)
     */
    public void changeTransparency(float step) {
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
        return Stage.parent.width;
    }

    /**
     * Return the height of the current costume or the pen size, when no costume is
     * available.
     *
     * @return the height of the sprite
     */
    public int getHeight() {
        return Stage.parent.height;
    }

    /**
     * Return the pixels of the current costume or an empty array, when no costume
     * is available.
     *
     * @return the pixels of the sprite
     */
    public int[] getPixels() {
        if (backdrops.size() == 0)
            return new int[0];

        return this.backdrops.get(this.currentBackdrop).getImage().pixels;
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
        if (name.equals("default"))
            return;

        this.timer.put(name, new Timer());
    }

    /**
     * Remove a timer by name. Removing of default is not permitted.
     *
     * @param name the name of the timer
     */
    public void removeTimer(String name) {
        if (name.equals("default"))
            return;

        this.timer.remove(name);
    }

    public void mouseEvent(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseDown = false;

        if (e.getAction() == MouseEvent.PRESS) {
            mouseDown = true;
        } else if (e.getAction() == MouseEvent.CLICK) {
            drawables.stream().forEach(d -> {
                if (d instanceof Sprite) {
                    Sprite s = (Sprite) d;
                    if (s.isTouchingMousePointer()) {
                        s.whenClicked();
                    }
                }
            });
        }
    }

    /**
     * Returns the current x-position of the mouse cursor
     *
     * @return x-position
     */
    public float getMouseX() {
        return mouseX;
    }

    /**
     * Returns the current y-position of the mouse cursor
     *
     * @return y-position
     */
    public float getMouseY() {
        return mouseY;
    }

    /**
     * Returns true is the mouse button is down
     *
     * @return mouse button down
     */
    public boolean isMouseDown() {
        return mouseDown;
    }

    public void whenKeyPressed(int keyCode) {}

    public void keyEvent(KeyEvent e) {
        switch (e.getAction()) {
            case KeyEvent.PRESS:
                this.whenKeyPressed(e.getKeyCode());
                keyCodePressed.put(e.getKeyCode(), true);
                break;
            case KeyEvent.RELEASE:
                keyCodePressed.put(e.getKeyCode(), false);
                break;
        }
    }

    /**
     * Returns true if the key is pressed
     *
     * @param keyCode a key code
     * @return key pressed
     */
    public boolean isKeyPressed(int keyCode) {
        Boolean isPressed = keyCodePressed.get(keyCode);
        if (isPressed == null) {
            return false;
        }
        return isPressed;
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

    public int pickRandom(int from, int to) {
        if (to < from) {
            return to + (int) (Math.random() * (from - to));
        }
        return from + (int) (Math.random() * (to - from));
    }

    /**
     * Set the desired frame rate of the stage.
     * The frame rate can drop below, but can not raise above.
     * The default frame rate is 60 FPS.
     * A lower frame does reduce the load on the CPU.
     *
     * @param frameRate Frame Rate in Seconds. For Example: 30
     */
    public void setFrameRate(int frameRate) {
        parent.frameRate(frameRate);
    }

    public void display(String text) {
        this.display.showText(text);
    }

    public void display(String text, int millis) {
        this.display.showText(text, millis);
    }

    /**
     * Draws the current backdrop or if none a solid color
     */
    public void pre() {
        // redraw background to clear screen
        Stage.parent.background(this.color.getRed(), this.color.getGreen(), this.color.getBlue());

        // draw current backdrop
        if (this.backdrops.size() > 0) {
            this.backdrops.get(this.currentBackdrop).drawAsBackground();
        }
        Stage.parent.image(penBuffer, Stage.parent.width / 2, Stage.parent.height / 2);
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

    public void run() {

    }

    public void draw() {
        for (Drawable d : this.drawables) {
            d.draw();
        }
        if (display != null) {
            this.display.draw();
        }

        if (debug) {
            parent.strokeWeight(1);
            parent.stroke(DEBUG_COLOR[0], DEBUG_COLOR[1], DEBUG_COLOR[2]);
            parent.fill(DEBUG_COLOR[0], DEBUG_COLOR[1], DEBUG_COLOR[2]);
            parent.line(mouseX, 0, mouseX, parent.height);
            parent.line(0, mouseY, parent.width, mouseY);
            parent.text("(" + mouseX + ", " + mouseY + ")", mouseX, mouseY);
        }
        this.run();
    }

    public static float[] rotateXY(float x, float y, float originX, float originY, float degrees) {
        float[] rotatedXY = new float[2];

        double radians = degrees * Math.PI / 180.0;
        x = x - originX;
        y = y - originY;
        rotatedXY[0] = (float) (x * Math.cos(radians) - y * Math.sin(radians)) + originX;
        rotatedXY[1] = (float) (x * Math.sin(radians) + y * Math.cos(radians)) + originY;

        return rotatedXY;
    }
}