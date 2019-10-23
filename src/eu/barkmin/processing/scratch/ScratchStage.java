package eu.barkmin.processing.scratch;

import com.sun.org.apache.xpath.internal.operations.Bool;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

/**
 * Represents the scratch stage. Only one object of this class can be created.
 */
public class ScratchStage {

    public static PApplet parent;
    private boolean debug;
    public static final int[] DEBUG_COLOR = {255, 0, 0};
    private static ScratchStage instance;
    private ArrayList<ScratchImage> backdrops = new ArrayList<>();
    private ScratchColor color = new ScratchColor();
    private int currentBackdrop = 0;
    private ArrayList<ScratchSound> sounds = new ArrayList<>();
    private PGraphics penBuffer;
    private HashMap<String, Timer> timer;
    private ArrayList<ScratchSprite> sprites;

    private float mouseX;
    private float mouseY;
    private boolean mouseDown;
    private HashMap<Integer, Boolean> keyCodePressed = new HashMap<>();

    private ScratchStage(PApplet parent, boolean debug) {
        parent.imageMode(PConstants.CENTER);
        parent.rectMode(PConstants.CENTER);
        ScratchStage.parent = parent;
        this.penBuffer = parent.createGraphics(parent.width, parent.height, parent.sketchRenderer());
        this.timer = new HashMap<>();
        this.timer.put("default", new Timer());
        this.debug = debug;
        this.sprites = new ArrayList<>();
    }

    /**
     * Returns the only instance of ScratchStage
     *
     * @return the ScratchStage object
     */
    public static ScratchStage getInstance() {
        return ScratchStage.instance;
    }

    public static void init(PApplet parent) {
        ScratchStage.init(parent, false);
    }

    public static void init(PApplet parent, boolean debug) {
        ScratchStage.instance = new ScratchStage(parent, debug);
        parent.registerMethod("pre", ScratchStage.instance);
        parent.registerMethod("draw", ScratchStage.instance);
        parent.registerMethod("mouseEvent", ScratchStage.instance);
        parent.registerMethod("keyEvent", ScratchStage.instance);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * Add a sprite to the stage.
     * @param sprite
     */
    public void addSprite(ScratchSprite sprite) {
        sprites.add(sprite);
    }

    /**
     * Lower a sprite.
     * @param sprite
     */
    public void lowerSprite(ScratchSprite sprite) {
        int index = sprites.indexOf(sprite);
        if (index > 0) {
            Collections.swap(sprites, index, index-1);
        }
    }

    /**
     * Rise a sprite.
     * @param sprite
     */
    public void raiseSprite(ScratchSprite sprite) {
        int index = sprites.indexOf(sprite);
        if (index > -1 && index < sprites.size() -1) {
            Collections.swap(sprites, index+1, index);
        }
    }

    public ArrayList<ScratchSprite> getSprites() {
        return sprites;
    }

    /**
     * Remove a sprite from the stage.
     * @param sprite
     */
    public void removeSprite(ScratchSprite sprite) {
        sprites.remove(sprite);
    }

    /**
     * Add a backdrop to the stage. If a backdrop with the received name already exists do nothing.
     *
     * @param name      a unique name
     * @param imagePath a image path
     */
    public void addBackdrop(String name, String imagePath) {
        for (ScratchImage backdrop : this.backdrops) {
            if (backdrop.getName().equals(name)) {
                return;
            }
        }
        this.backdrops.add(new ScratchImage(name, imagePath));
    }

    /**
     * Remove a backdrop from the stage.
     * @param name of the backdrop
     */
    public void removeBackdrop(String name) {
        for (int i = 0; i < this.backdrops.size(); i++) {
            ScratchImage backdrop = this.backdrops.get(i);
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
            ScratchImage backdrop = backdrops.get(i);
            if (backdrop.getName().equals(name)) {
                this.currentBackdrop = i;
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
     * Add a sound to the stage. If a sound with the received name already exists do nothing.
     *
     * @param name      a unique name
     * @param soundPath a sound path
     */
    public void addSound(String name, String soundPath) {
        for (ScratchSound sound : this.sounds) {
            if (sound.getName().equals(name)) {
                return;
            }
        }

        this.sounds.add(new ScratchSound(name, soundPath));
    }

    /**
     * Remove a sound from the stage.
     *
     * @param name the sound name
     */
    public void removeSound(String name) {
        for (int i = 0; i < this.sounds.size(); i++) {
            ScratchSound sound = this.sounds.get(i);
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
        for (ScratchSound sound : sounds) {
            if (sound.getName().equals(name) && !sound.isPlaying()) {
                sound.play();
            }
        }
    }

    /**
     * Stops the playing of all sounds of the stage.
     */
    public void stopAllSounds() {
        for (ScratchSound sound : sounds) {
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
     * @see ScratchImage#setTint(float, float, float)
     */
    public void setTint(int r, int g, int b) {
        if (this.backdrops.size() == 0) return;
        this.backdrops.get(this.currentBackdrop).setTint(r, g, b);
    }

    /**
     * Sets the tint for the current backdrop with a hue.
     *
     * @see ScratchImage#setTint(float)
     */
    public void setTint(float h) {
        if (this.backdrops.size() == 0) return;
        this.backdrops.get(this.currentBackdrop).setTint(h);
    }

    /**
     * Changes the tint for the current backdrop
     *
     * @see ScratchImage#changeTint(float)
     */
    public void changeTint(float step) {
        if (this.backdrops.size() == 0) return;

        this.backdrops.get(this.currentBackdrop).changeTint(step);
    }

    /**
     * Sets the transparency of the current backdrop.
     *
     * @see ScratchImage#setTransparency(float)
     */
    public void setTransparency(float transparency) {
        this.backdrops.get(this.currentBackdrop).setTransparency(transparency);
    }

    /**
     * Changes the transparency for the current costume.
     *
     * @see ScratchImage#changeTransparency(float)
     */
    public void changeTransparency(float step) {
        if (this.backdrops.size() == 0) return;

        this.backdrops.get(this.currentBackdrop).changeTransparency(step);
    }

    /**
     * Return the width of the current costume or the pen size, when no costume is available.
     *
     * @return the width of the sprite
     */
    public int getWidth() {
        return ScratchStage.parent.width;
    }

    /**
     * Return the height of the current costume or the pen size, when no costume is available.
     *
     * @return the height of the sprite
     */
    public int getHeight() {
        return ScratchStage.parent.height;
    }

    /**
     * Return the pixels of the current costume or an empty array, when no costume is available.
     *
     * @return the pixels of the sprite
     */
    public int[] getPixels() {
        if (backdrops.size() == 0) return new int[0];

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
        if (name.equals("default")) return;

        this.timer.put(name, new Timer());
    }

    /**
     * Remove a timer by name. Removing of default is not permitted.
     *
     * @param name the name of the timer
     */
    public void removeTimer(String name) {
        if (name.equals("default")) return;

        this.timer.remove(name);
    }

    public void mouseEvent(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseDown = false;

        if (e.getAction() == MouseEvent.PRESS) {
            mouseDown = true;
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

    public void keyEvent(KeyEvent e) {
        switch (e.getAction()) {
            case KeyEvent.PRESS:
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
     * Returns the current date
     *
     * @return current date
     */
    public int getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.getDayOfMonth();
    }

    /**
     * Returns the current week
     *
     * @return current week
     */
    public int getCurrentDayOfWeek() {
        LocalDateTime now = LocalDateTime.now();
        return now.getDayOfWeek().getValue();
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
        Period p = Period.between(now, then);
        return p.getDays();
    }

    public int pickRandom(int from, int to) {
        if (to < from) {
            return to + (int) (Math.random() * (from - to));
        }
        return from + (int) (Math.random() * (to - from));
    }

    /**
     * Draws the current backdrop or if none a solid color
     */
    public void pre() {
        // redraw background to clear screen

        ScratchStage.parent.background(this.color.getRed(), this.color.getGreen(), this.color.getBlue());

        // draw current backdrop
        if (this.backdrops.size() > 0) {
            this.backdrops.get(this.currentBackdrop).drawAsBackground();
        }
        ScratchStage.parent.image(penBuffer, ScratchStage.parent.width / 2, ScratchStage.parent.height / 2);
    }

    public void draw() {
        for(ScratchSprite s : sprites) {
            s.draw();
        }
        if (debug) {
            parent.strokeWeight(1);
            parent.stroke(DEBUG_COLOR[0], DEBUG_COLOR[1], DEBUG_COLOR[2]);
            parent.fill(DEBUG_COLOR[0], DEBUG_COLOR[1], DEBUG_COLOR[2]);
            parent.line(mouseX, 0, mouseX, parent.height);
            parent.line(0, mouseY, parent.width, mouseY);
            parent.text("(" + mouseX + ", " + mouseY + ")", mouseX, mouseY);
        }
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
