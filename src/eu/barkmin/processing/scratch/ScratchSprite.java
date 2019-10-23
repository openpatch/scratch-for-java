package eu.barkmin.processing.scratch;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashMap;

public class ScratchSprite {
    private ArrayList<ScratchImage> costumes = new ArrayList<>();
    private int currentCostume = 0;
    private ArrayList<ScratchSound> sounds = new ArrayList<>();
    private boolean show = true;
    private float size = 100;
    private boolean onEdgeBounce = false;
    private float rotation = 0;
    private float x = 0;
    private float y = 0;
    private HashMap<String, Timer> timer;
    private ScratchPen pen;
    private ScratchHitbox hitbox;

    public ScratchSprite() {
        this.pen = new ScratchPen();
        this.x = ScratchStage.parent.width / 2.0f;
        this.y = ScratchStage.parent.height / 2.0f;
        this.timer = new HashMap<>();
        this.timer.put("default", new Timer());

        ScratchStage.parent.registerMethod("keyEvent", this);
        ScratchStage.parent.registerMethod("mouseEvent", this);
    }

    public ScratchSprite(String name, String imagePath) {
        this();
        ScratchImage costume = new ScratchImage(name, imagePath);
        this.costumes.add(costume);
    }

    /**
     * Copies a ScratchSprite object.
     *
     * @param s a ScratchSprite object to copy
     */
    public ScratchSprite(ScratchSprite s) {
        this.costumes = new ArrayList<>();
        for (ScratchImage costume : s.costumes) {
            this.costumes.add(new ScratchImage(costume));
        }
        this.currentCostume = s.currentCostume;
        this.sounds = new ArrayList<>();
        for (ScratchSound sound : s.sounds) {
            this.sounds.add(new ScratchSound(sound));
        }
        this.show = s.show;
        this.size = s.size;
        this.onEdgeBounce = s.onEdgeBounce;
        this.rotation = s.rotation;
        this.x = s.x;
        this.y = s.y;
        this.timer = new HashMap<>();
        this.timer.put("default", new Timer());
        this.pen = new ScratchPen(s.pen);
    }

    /**
     * Add a costume to the sprite. If a costume with the received name already exists do nothing.
     *
     * @param name      a unique name
     * @param imagePath a image path
     */
    public void addCostume(String name, String imagePath) {
        for (ScratchImage costume : this.costumes) {
            if (costume.getName().equals(name)) {
                return;
            }
        }

        this.costumes.add(new ScratchImage(name, imagePath));
    }

    /**
     * Switch to a costume by name.
     *
     * @param name the name of a costume
     */
    public void switchCostume(String name) {
        for (int i = 0; i < costumes.size(); i++) {
            ScratchImage costume = costumes.get(i);
            if (costume.getName().equals(name)) {
                this.currentCostume = i;
                return;
            }
        }
    }

    /**
     * Switch to the next costume.
     */
    public void nextCostume() {
        this.currentCostume = (this.currentCostume + 1) % costumes.size();
    }

    /**
     * Returns the current costume name
     *
     * @return a costume name
     */
    public String getCurrentCostumeName() {
        if (costumes.size() == 0) return null;

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
     * Remove a sound from the sprite.
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
        for (int i = 0; i < sounds.size(); i++) {
            ScratchSound sound = sounds.get(i);
            if (sound.getName().equals(name) && !sound.isPlaying()) {
                sound.play();
            }
        }
    }

    /**
     * Stops the playing of all sounds of the sprite.
     */
    public void stopAllSounds() {
        for (ScratchSound sound : sounds) {
            sound.stop();
        }
    }

    /**
     * Sets the tint for the sprite with rgb.
     *
     * @see ScratchImage#setTint(float, float, float)
     */
    public void setTint(int r, int g, int b) {
        if (costumes.size() == 0) return;

        for (ScratchImage costume : this.costumes) {
            costume.setTint(r, g, b);
        }
    }

    /**
     * Sets the tint for the sprite with a hue.
     *
     * @see ScratchImage#setTint(float)
     */
    public void setTint(float h) {
        if (costumes.size() == 0) return;

        for (ScratchImage costume : this.costumes) {
            costume.setTint(h);
        }
    }

    /**
     * Changes the tint for the sprite.
     *
     * @see ScratchImage#changeTint(float)
     */
    public void changeTint(float step) {
        if (costumes.size() == 0) return;

        for (ScratchImage costume : this.costumes) {
            costume.changeTint(step);
        }
    }

    /**
     * Sets the transparency of the sprite.
     *
     * @see ScratchImage#setTransparency(float)
     */
    public void setTransparency(float transparency) {
        if (costumes.size() == 0) return;

        for (ScratchImage costume : this.costumes) {
            costume.setTransparency(transparency);
        }
    }

    /**
     * Changes the transparency for the sprite.
     *
     * @see ScratchImage#changeTransparency(float)
     */
    public void changeTransparency(float step) {
        if (costumes.size() == 0) return;

        for (ScratchImage costume : this.costumes) {
            costume.changeTransparency(step);
        }
    }

    /**
     * Hides the sprite. The pen is not effected.
     */
    public void hide() {
        this.show = false;
    }

    /**
     * Shows the sprite.
     */
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
     * Returns the size of the sprite
     *
     * @return size in percentage
     */
    public float getSize() {
        return this.size;
    }

    /**
     * Sets the size of the sprite
     *
     * @param percentage a percentage
     */
    public void setSize(float percentage) {
        this.size = percentage;
    }

    /**
     * Sets if the sprite should bounce when hitting the edge of the screen.
     *
     * @param b
     */
    public void setOnEdgeBounce(boolean b) {
        this.onEdgeBounce = b;
    }

    /**
     * Sets the position of the sprite
     *
     * @param x a x coordinate
     * @param y a y coordinate
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.getPen().setPosition(x, y);
    }

    public void setPosition(float x, float y) {
        this.setPosition(Math.round(x), Math.round(y));
    }

    public void setPosition(double x, double y) {
        this.setPosition((float) x, (float) y);
    }

    /**
     * Rotates the sprite by a certain degrees to the left.
     *
     * @param degrees
     */
    public void turnLeft(float degrees) {
        this.setRotation(this.rotation - degrees);
    }

    /**
     * Rotates the sprite by a certain degrees to the right.
     *
     * @param degrees
     */
    public void turnRight(float degrees) {
        this.setRotation(this.rotation + degrees);
    }

    /**
     * Sets the rotation of the sprite to a given degrees. When this value is 0 the sprite move right, when it is 180 is moves to the left.
     *
     * @param degrees
     */
    public void setRotation(float degrees) {
        this.rotation = degrees;
        if (this.rotation < 0) {
            this.rotation += 360;
        }
    }

    /**
     * Returns the rotation of the sprite.
     *
     * @return the rotation [0...360]
     */
    public float getRotation() {
        return this.rotation;
    }

    /**
     * Returns the pen of the sprite.
     *
     * @return
     */
    public ScratchPen getPen() {
        return this.pen;
    }

    /**
     * Moves the sprite towards the current rotation by the received steps.
     *
     * @param steps a number of pixels
     */
    public void move(float steps) {
        PApplet parent = ScratchStage.parent;

        // convert degrees to radians
        float newX = steps * (float) Math.cos(this.rotation * Math.PI / 180) + this.x;
        float newY = steps * (float) Math.sin(this.rotation * Math.PI / 180) + this.y;

        ScratchImage currentCostume = null;
        if (this.costumes.size() > 0) {
            currentCostume = this.costumes.get(this.currentCostume);
        }
        float costumeWidth = currentCostume != null ? currentCostume.getImage().width : this.pen.getSize();
        float costumeHeight = currentCostume != null ? currentCostume.getImage().height : this.pen.getSize();

        if (this.onEdgeBounce) {
            float spriteWidth = this.show ? costumeWidth : this.pen.getSize();
            if (newX > parent.width - spriteWidth / 2 || newX < spriteWidth / 2) {
                this.rotation = this.calculateAngleOfReflection(this.rotation, false);
            }

            float spriteHeight = this.show ? costumeHeight : this.pen.getSize();
            if (newY > parent.height - spriteHeight / 2 || newY < spriteHeight / 2) {
                this.rotation = this.calculateAngleOfReflection(this.rotation, true);
            }
        }

        this.x = newX;
        this.y = newY;

        this.pen.setPosition(this.x, this.y);
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
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Changes x by a certain amount
     *
     * @param x number in pixels
     */
    public void changeX(float x) {
        this.x += x;
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
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Changes y by a certain amount
     *
     * @param y number in pixels
     */
    public void changeY(float y) {
        this.y += y;
    }

    /**
     * Return the width of the current costume or the pen size, when no costume is available.
     *
     * @return the width of the sprite
     */
    public int getWidth() {
        if (costumes.size() == 0) return (int) this.getPen().getSize();

        return this.costumes.get(this.currentCostume).getImage().width;
    }

    /**
     * Return the height of the current costume or the pen size, when no costume is available.
     *
     * @return the height of the sprite
     */
    public int getHeight() {
        if (costumes.size() == 0) return (int) this.getPen().getSize();

        return this.costumes.get(this.currentCostume).getImage().height;
    }

    /**
     * Return the pixels of the current costume or an empty array, when no costume is available.
     *
     * @return the pixels of the sprite
     */
    public int[] getPixels() {
        if (costumes.size() == 0) return new int[0];

        return this.costumes.get(this.currentCostume).getImage().pixels;
    }

    /**
     * Return the pixelWidth of the current costume or an empty array, when no costume is available. Only useful when using the getPixels method. Otherwise use getWidth instead.
     *
     * @return the pixel width of the sprite
     */
    public int getPixelWidth() {
        return this.costumes.get(this.currentCostume).getImage().pixelWidth;
    }

    /**
     * Return the pixelHeight of the current costume or the pen size, when no costume is available. Only usefule when using the getPixels method. Otherwise use getHeight instead.
     *
     * @return the pixel height of the sprite
     */
    public int getPixelHeight() {
        return this.costumes.get(this.currentCostume).getImage().pixelHeight;
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

    private float calculateAngleOfReflection(float angleOfIncidence, boolean horizontalWall) {
        if (horizontalWall) {
            float angleOfReflection = 360 - angleOfIncidence;
            while (angleOfReflection < 0) angleOfReflection += 360;
            return angleOfReflection;
        } else {
            float angleOfReflection = 180 - angleOfIncidence;
            while (angleOfReflection < 0) angleOfReflection += 360;
            return angleOfReflection;
        }
    }

    /**
     * Returns true is the mouse pointer is touching a non transparent area of the sprite.
     *
     * @return true if touching
     */
    public boolean isTouchingMousePointer() {
        float topLeftCornerX = x - getWidth() / 2.0f;
        float topLeftCornerY = y - getHeight() / 2.0f;

        float bottomRightCornerX = x + getWidth() / 2.0f;
        float bottomRightCornerY = y + getHeight() / 2.0f;

        float[] mouse = ScratchStage.rotateXY(getMouseX(), getMouseY(), x, y, -rotation);

        boolean touching = mouse[0] > topLeftCornerX && mouse[1] > topLeftCornerY &&
                mouse[0] < bottomRightCornerX && mouse[1] < bottomRightCornerY;

        if (touching) {
            int relativeMouseX = Math.round(mouse[0] - topLeftCornerX);
            int relativeMouseY = Math.round(mouse[1] - topLeftCornerY);

            int color = this.costumes.get(this.getCurrentCostumeIndex()).getImage().get(relativeMouseX, relativeMouseY);
            return ScratchStage.parent.alpha(color) != 0;
        }

        return false;
    }


    /**
     * Returns true if the rectangle which contains the image is outside of the stage
     *
     * @return true if outside
     */
    public boolean isTouchingEdge() {
        ScratchImage currentCostume = this.costumes.get(this.getCurrentCostumeIndex());
        PApplet parent = ScratchStage.parent;
        float costumeWidth = currentCostume != null ? currentCostume.getImage().width : this.pen.getSize();
        float costumeHeight = currentCostume != null ? currentCostume.getImage().height : this.pen.getSize();
        float spriteWidth = this.show ? costumeWidth : this.pen.getSize();
        float spriteHeight = this.show ? costumeHeight : this.pen.getSize();

        float[] cornerTopLeft = ScratchStage.rotateXY(x - spriteWidth / 2.0f, y - spriteHeight / 2.0f, x, y, rotation);
        float[] cornerTopRight = ScratchStage.rotateXY(x + spriteWidth / 2.0f, y - spriteHeight / 2.0f, x, y, rotation);
        float[] cornerBottomLeft = ScratchStage.rotateXY(x - spriteWidth / 2.0f, y + spriteHeight / 2.0f, x, y, rotation);
        float[] cornerBottomRight = ScratchStage.rotateXY(x + spriteWidth / 2.0f, y + spriteHeight / 2.0f, x, y, rotation);

        float[][] corners = {cornerTopLeft, cornerTopRight, cornerBottomLeft, cornerBottomRight};

        for (float[] corner : corners) {
            float cornerX = corner[0];
            float cornerY = corner[1];
            if (cornerX > parent.width || cornerX < 0) {
                return true;
            }

            if (cornerY > parent.height || cornerY < 0) {
                return true;
            }
        }

        return false;

    }

    public void setHitbox(int[] xPoints, int[] yPoints) {
        this.hitbox = new ScratchHitbox(xPoints, yPoints);
    }

    public ScratchHitbox getHitbox() {
        ScratchImage currentCostume = null;
        if (this.costumes.size() > this.getCurrentCostumeIndex()) {
            currentCostume = this.costumes.get(this.getCurrentCostumeIndex());
        }
        float costumeWidth = currentCostume != null ? currentCostume.getImage().width : this.pen.getSize();
        float costumeHeight = currentCostume != null ? currentCostume.getImage().height : this.pen.getSize();
        float spriteWidth = this.show ? costumeWidth : this.pen.getSize();
        float spriteHeight = this.show ? costumeHeight : this.pen.getSize();

        if (this.hitbox != null) {
            this.hitbox.translateAndRotateAndResize(
                    rotation, x, y,
                    x - spriteWidth / 2.0f, y - spriteHeight / 2.0f,
                    size);
            return this.hitbox;
        }

        float[] cornerTopLeft = ScratchStage.rotateXY(x - spriteWidth / 2.0f, y - spriteHeight / 2.0f, x, y, rotation);
        float[] cornerTopRight = ScratchStage.rotateXY(x + spriteWidth / 2.0f, y - spriteHeight / 2.0f, x, y, rotation);
        float[] cornerBottomLeft = ScratchStage.rotateXY(x - spriteWidth / 2.0f, y + spriteHeight / 2.0f, x, y, rotation);
        float[] cornerBottomRight = ScratchStage.rotateXY(x + spriteWidth / 2.0f, y + spriteHeight / 2.0f, x, y, rotation);

        int[] xPoints = new int[4];
        int[] yPoints = new int[4];
        xPoints[0] = Math.round(cornerTopLeft[0]);
        yPoints[0] = Math.round(cornerTopLeft[1]);
        xPoints[1] = Math.round(cornerTopRight[0]);
        yPoints[1] = Math.round(cornerTopRight[1]);
        xPoints[2] = Math.round(cornerBottomRight[0]);
        yPoints[2] = Math.round(cornerBottomRight[1]);
        xPoints[3] = Math.round(cornerBottomLeft[0]);
        yPoints[3] = Math.round(cornerBottomLeft[1]);

        return new ScratchHitbox(xPoints, yPoints);
    }

    public boolean isTouchingSprite(ScratchSprite sprite) {
        return this.getHitbox().intersects(sprite.getHitbox());
    }

    /**
     * Returns the current x-position of the mouse cursor
     *
     * @return x-position
     */
    public float getMouseX() {
        return ScratchStage.getInstance().getMouseX();
    }

    /**
     * Returns the current y-position of the mouse cursor
     *
     * @return y-position
     */
    public float getMouseY() {
        return ScratchStage.getInstance().getMouseY();
    }

    /**
     * Returns true is the mouse button is down
     *
     * @return mouse button down
     */
    public boolean isMouseDown() {
        return ScratchStage.getInstance().isMouseDown();
    }

    /**
     * Returns true if the key is pressed
     *
     * @param keyCode a key code
     * @return key pressed
     */
    public boolean isKeyPressed(int keyCode) {
        return ScratchStage.getInstance().isKeyPressed(keyCode);
    }

    /**
     * Returns the current year
     *
     * @return current year
     */
    public int getCurrentYear() {
        return ScratchStage.getInstance().getCurrentYear();
    }

    /**
     * Returns the current date
     *
     * @return current date
     */
    public int getCurrentDate() {
        return ScratchStage.getInstance().getCurrentDate();
    }

    /**
     * Returns the current week
     *
     * @return current week
     */
    public int getCurrentDayOfWeek() {
        return ScratchStage.getInstance().getCurrentDayOfWeek();
    }

    /**
     * Returns the current hour
     *
     * @return current hour
     */
    public int getCurrentHour() {
        return ScratchStage.getInstance().getCurrentHour();
    }

    /**
     * Returns the current minute
     *
     * @return current minute
     */
    public int getCurrentMinute() {
        return ScratchStage.getInstance().getCurrentMinute();
    }

    /**
     * Returns the current second
     *
     * @return current second
     */
    public int getCurrentSecond() {
        return ScratchStage.getInstance().getCurrentSecond();
    }

    /**
     * Returns the current millisecond
     *
     * @return current millisecond
     */
    public int getCurrentMillisecond() {
        return ScratchStage.getInstance().getCurrentMillisecond();
    }

    /**
     * Returns the days since 2010/01/01
     *
     * @return days since 2010/01/01
     */
    public int getDaysSince2000() {
        return ScratchStage.getInstance().getDaysSince2000();
    }

    public void keyEvent(KeyEvent e) {
        this.whenKeyPressed(e.getKeyCode());
    }

    public void whenKeyPressed(int keyCode) {

    }

    public void mouseEvent(MouseEvent e) {
        this.whenMouseMoved(e.getX(), e.getY());
    }

    public void whenMouseMoved(float x, float y) {

    }

    public int pickRandom(int from, int to) {
        return ScratchStage.getInstance().pickRandom(from, to);
    }

    /**
     * Draws the sprite if it is not hidden.
     */
    public void draw() {
        this.pen.draw();
        if (costumes.size() > 0 && this.show) {
            this.costumes.get(this.currentCostume).draw(this.size, this.rotation, this.x, this.y);
        }

        if (ScratchStage.getInstance().isDebug()) {
            this.getHitbox().draw();
        }
        this.run();
    }

    public void run() {

    }
}
