package eu.barkmin.processing.scratch;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.ArrayList;

/**
 * Represents the scratch stage. Only one object of this class can be created.
 */
public class ScratchStage {

    public static PApplet parent;
    private static ScratchStage instance;
    private ArrayList<ScratchImage> backdrops = new ArrayList<>();
    private ScratchColor color = new ScratchColor();
    private int currentBackdrop = 0;
    private ArrayList<ScratchSound> sounds = new ArrayList<>();
    private PGraphics penBuffer;

    private ScratchStage(PApplet parent) {
        parent.imageMode(PConstants.CENTER);
        ScratchStage.parent = parent;
        this.penBuffer = parent.createGraphics(parent.width, parent.height);
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
        ScratchStage.instance = new ScratchStage(parent);
        parent.registerMethod("pre", ScratchStage.instance);
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
        this.penBuffer = parent.createGraphics(parent.width, parent.height);
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
        for (int i = 0; i < sounds.size(); i++) {
            ScratchSound sound = sounds.get(i);
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
}
