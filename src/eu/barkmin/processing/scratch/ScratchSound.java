package eu.barkmin.processing.scratch;

import processing.sound.*;

public class ScratchSound {
    private String name;
    private SoundFile sound;
    private float volume;

    public ScratchSound(String name, String soundPath) {
        this.name = name;
        this.sound = new SoundFile(ScratchStage.parent, soundPath);
    }

    /**
     * Copies a ScratchSound object.
     *
     * @param s ScratchSound object to copy
     */
    public ScratchSound(ScratchSound s) {
        this.name = s.name;
        this.sound = s.sound;
    }

    /**
     * Returns the name
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Starts playing the sound file.
     */
    public void play() {
        if (this.sound != null) {
            this.sound.play();
        }
    }

    /**
     * Pauses the playing of the sound file.
     */
    public void pause() {
        if (this.sound != null) {
            this.sound.pause();
        }
    }

    /**
     * Stops the playing of the sound file.
     */
    public void stop() {
        if (this.sound != null) {
            this.sound.stop();
        }
    }

    /**
     * Return true if the sound file is playing.
     *
     * @return sound file is playing
     */
    public boolean isPlaying() {
        if (this.sound != null) {
            return this.sound.isPlaying();
        }
        return false;
    }

    /**
     * Sets the volume
     *
     * @param amp A volume [0...1]
     */
    public void setVolume(float amp) {
        this.volume = amp;
        if (amp <= 1 && amp >= 0) {
            this.sound.amp(amp);
        }
    }

    /**
     * Changes the volume by a step
     * @param step
     */
    public void changeVolume(float step) {
        float newVolume = this.volume + step;
        if (newVolume <= 1 && newVolume >= 0) {
            this.volume = newVolume;
        }
    }

    /**
     * Returns the volume
     * @return the volume
     */
    public float getVolume() {
        return this.volume;
    }
}
