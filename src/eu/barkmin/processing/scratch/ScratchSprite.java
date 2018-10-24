package eu.barkmin.processing.scratch;

import processing.core.PApplet;

import java.util.ArrayList;

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
    private ScratchPen pen;

    public ScratchSprite(String name, String imagePath) {
        ScratchImage costume = new ScratchImage(name, imagePath);
        this.costumes.add(costume);
        this.pen = new ScratchPen();
        this.x = ScratchStage.parent.width / 2.0f;
        this.y = ScratchStage.parent.height / 2.0f;
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
     * Sets the tint for the current costume with rgb.
     *
     * @see ScratchImage#setTint(float, float, float)
     */
    public void setTint(int r, int g, int b) {
        if (costumes.size() == 0) return;

        this.costumes.get(this.currentCostume).setTint(r, g, b);
    }

    /**
     * Sets the tint for the current costume with a hue.
     *
     * @see ScratchImage#setTint(float)
     */
    public void setTint(float h) {
        if (costumes.size() == 0) return;

        this.costumes.get(this.currentCostume).setTint(h);
    }

    /**
     * Changes the tint for the current costume.
     *
     * @see ScratchImage#changeTint(float)
     */
    public void changeTint(float step) {
        if (costumes.size() == 0) return;

        this.costumes.get(this.currentCostume).changeTint(step);
    }

    /**
     * Sets the transparency of the current costume.
     *
     * @see ScratchImage#setTransparency(float)
     */
    public void setTransparency(float transparency) {
        if (costumes.size() == 0) return;

        this.costumes.get(this.currentCostume).setTransparency(transparency);
    }

    /**
     * Changes the transparency for the current costume.
     *
     * @see ScratchImage#changeTransparency(float)
     */
    public void changeTransparency(float step) {
        if (costumes.size() == 0) return;

        this.costumes.get(this.currentCostume).changeTransparency(step);
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
    }

    /**
     * Rotates the sprite by a certain degrees to the left.
     *
     * @param degrees
     */
    public void turnLeft(float degrees) {
        this.rotation = (this.rotation - degrees) % 360;
    }

    /**
     * Rotates the sprite by a certain degrees to the right.
     *
     * @param degrees
     */
    public void turnRight(float degrees) {
        this.rotation = (this.rotation + degrees) % 360;
    }

    /**
     * Sets the rotation of the sprite to a given degrees. When this value is 0 the sprite move right, when it is 180 is moves to the left.
     *
     * @param degrees
     */
    public void setRotation(float degrees) {
        this.rotation = degrees;
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

        ScratchImage currentCostume = this.costumes.get(this.currentCostume);

        if (this.onEdgeBounce) {
            float spriteWidth = this.show ? currentCostume.getImage().width : this.pen.getSize();
            if (newX > parent.width - spriteWidth / 2 || newX < spriteWidth / 2) {
                this.rotation = this.calculateAngleOfReflection(this.rotation, false);
            }

            float spriteHeight = this.show ? currentCostume.getImage().height : this.pen.getSize();
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
     * @param x a x coordinate
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Changes x by a certain amount
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
     * @param y a y coordinate
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Changes y by a certain amount
     * @param y number in pixels
     */
    public void changeY(float y) {
        this.y += y;
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
     * Draws the sprite if it is not hidden.
     */
    public void draw() {
        this.pen.draw();
        if (costumes.size() > 0 && this.show) {
            this.costumes.get(this.currentCostume).draw(this.size, this.x, this.y);
        }
    }
}
