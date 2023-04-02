package org.openpatch.scratch;

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

/**
 * Represents a scratch stage.
 */
public class Stage {

    private CopyOnWriteArrayList<Image> backdrops = new CopyOnWriteArrayList<>();
    private Color color = new Color();
    private int currentBackdrop = 0;
    private CopyOnWriteArrayList<Sound> sounds = new CopyOnWriteArrayList<>();
    private PGraphics penBuffer;
    private Text display;
    private ConcurrentHashMap<String, Timer> timer;
    CopyOnWriteArrayList<Drawable> drawables;
    private float mouseX;
    private float mouseY;
    private boolean mouseDown;
    private ConcurrentHashMap<Integer, Boolean> keyCodePressed = new ConcurrentHashMap<>();

    public Stage() {
        this(480, 360);
    }

    public Stage(int width, int height) {
        this(width, height, false);
    }

    public Stage(int width, int height, boolean debug) {
        this.drawables = new CopyOnWriteArrayList<>();
        this.timer = new ConcurrentHashMap<>();
        if (Window.getInstance() == null) {
            new Window(width, height);
            Applet a = Applet.getInstance();
            a.setDebug(debug);
            a.addStage("main", this);
        }
        Applet applet = Applet.getInstance();
        this.penBuffer = applet.createGraphics(applet.width, applet.height, applet.sketchRenderer());
        /**
        * Smooth does currently not work on Apple Silicon
        * https://github.com/processing/processing4/issues/694
        */
        this.penBuffer.smooth(8);
        this.timer.put("default", new Timer());
        this.display = new Text(null, 0, applet.height, true, TextStyle.BOX);
        this.display.addedToStage(this);

    }

    /**
     * @deprecated since v3.2.0: Use stage.getWindow().setDebug(debug) instead
     */
    public void setDebug(boolean debug) {
        Applet.getInstance().setDebug(debug);
    }

    /**
     * @deprecated since v3.2.0: Use stage.getWindow().isDebug() instead
     */
    public boolean isDebug() {
        return Applet.getInstance().isDebug();
    }

    /**
     * Add a sprite, text, pen or image to the stage.
     *
     * @param drawable
     */
    public void add(Drawable drawable) {
        drawables.add(drawable);
        drawable.addedToStage(this);
    }

    public void goLayersBackwards(Drawable drawable, int number) {
        int index = drawables.indexOf(drawable);
        if (index == -1)
            return;
        int newIndex = index - number;
        if (newIndex < 0)
            newIndex = 0;
        newIndex = Math.min(newIndex, drawables.size() - 1);
        drawables.remove(index);
        drawables.add(newIndex, drawable);
    }

    public void goLayersForwards(Drawable drawable, int number) {
        int index = drawables.indexOf(drawable);
        if (index == -1)
            return;
        int newIndex = index + number;
        if (newIndex < 0)
            newIndex = 0;
        newIndex = Math.min(newIndex, drawables.size() - 1);
        drawables.remove(index);
        drawables.add(newIndex, drawable);
    }

    public void goToFrontLayer(Drawable drawable) {
        drawables.remove(drawable);
        drawables.add(drawable);
    }

    public void goToBackLayer(Drawable drawable) {
        drawables.remove(drawable);
        drawables.add(0, drawable);
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
        drawable.removedFromStage(this);
    }

    public void removeAll() {
        for (Drawable drawable : this.drawables) {
            drawable.removedFromStage(this);
        }
        drawables.clear();
    }

    public void remove(Class<? extends Drawable> c) {
        for (Drawable drawable : this.drawables) {
            if (c.isInstance(drawable)) {
                drawable.removedFromStage(this);
            }
        }
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
        Image backdrop = new Image(name, imagePath);
        this.backdrops.add(backdrop);
        backdrop.addedToStage(this);
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
                this.emitBackdropSwitch();
                return;
            }
        }
    }

    private void emitBackdropSwitch() {
        Image backdrop = backdrops.get(this.currentBackdrop);
        String name = backdrop.getName();
        drawables.stream().forEach(d -> {
            if (d instanceof Sprite) {
                Sprite s = (Sprite) d;
                s.whenBackdropSwitches(name);
            }
        });
        this.whenBackdropSwitches(name);

    }

    public void whenBackdropSwitches(String name) {

    }

    /**
     * Switch to the next backdrop.
     */
    public void nextBackdrop() {
        this.currentBackdrop = (this.currentBackdrop + 1) % backdrops.size();
        this.emitBackdropSwitch();
    }

    /**
     * Switch to the previous backdrop.
     */
    public void previousBackdrop() {
        this.currentBackdrop = (this.currentBackdrop - 1) % backdrops.size();
        this.emitBackdropSwitch();
    }

    /**
     * Switch to a random backdrop.
     */
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

    /**
     * Erases all lines on the pen layer.
     */
    public void eraseAll() {
        try {
            this.penBuffer = Applet.getInstance().createGraphics(this.getWidth(), this.getHeight());
        } catch (Exception e) {
        }
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
     * Stops the playing of the sound with the given name
     *
     * @param name Name of the sound
     */
    public void stopSound(String name) {
        for (Sound sound : sounds) {
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
        for (Sound sound : sounds) {
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

    public void changeColor(double h) {
        this.changeColor((float) h);
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
     * @deprecated since v3.2.0: Use Window.getInstance().getWidth() instead
     *             Return the width of the current costume or the pen size, when no
     *             costume is
     *             available.
     *
     * @return the width of the sprite
     */
    public int getWidth() {
        return Applet.getInstance().getWidth();
    }

    /**
     * @deprecated since v3.2.0: Use Window.getInstance().getHeight() instead
     *             Return the height of the current costume or the pen size, when no
     *             costume is
     *             available.
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

    public void whenKeyPressed(int keyCode) {
    }

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
        Applet applet = Applet.getInstance();
        if (applet == null)
            return;
        // redraw background to clear screen
        applet.background(this.color.getRed(), this.color.getGreen(), this.color.getBlue());

        // draw current backdrop
        if (this.backdrops.size() > 0) {
            this.backdrops.get(this.currentBackdrop).drawAsBackground();
        }
        if (penBuffer.pixels != null) {
            applet.image(penBuffer, applet.width / 2, applet.height / 2);
        } else {
            try {
                penBuffer.loadPixels();
            } catch (Exception e) {
            }
        }
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
        Applet applet = Applet.getInstance();
        if (applet == null)
            return;
        for (Drawable d : this.drawables) {
            d.draw();
        }
        if (display != null) {
            this.display.draw();
        }

        if (applet.isDebug()) {
            applet.strokeWeight(1);
            applet.stroke(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
            applet.fill(Window.DEBUG_COLOR[0], Window.DEBUG_COLOR[1], Window.DEBUG_COLOR[2]);
            applet.line(mouseX, 0, mouseX, applet.height);
            applet.line(0, mouseY, applet.width, mouseY);
            applet.text("(" + mouseX + ", " + mouseY + ")", mouseX, mouseY);
            applet.text("FPS: " + Math.round(applet.frameRate * 100) / 100, 20, 10);
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
