package org.openpatch.scratch.extensions.sound;

import org.openpatch.scratch.internal.Sound;

/**
 * One play of a sound, with its own volume and stop button. Obtain it from
 * {@code sprite.playSoundCopy("hit", 30)} or {@code stage.playSoundCopy("hit", 30)}.
 * Completed or stopped playbacks cannot be restarted; play the sound again
 * to get a new handle. Closing a handle stops only that playback.
 *
 *
 *
   * @example.files SoundPlaybackExample.java
 */
public final class SoundPlayback implements AutoCloseable {
  private final Sound sound;

  private SoundPlayback(Sound source, double volume) {
    this.sound = new Sound(source);
    this.setVolume(volume);
    this.sound.play();
  }

  /** @ignore-in-docs Used by the stage and sprite playback registries. */
  public static SoundPlayback start(Sound source, double volume) {
    return new SoundPlayback(source, volume);
  }

  /**
   * @return the name of the sound
   * @example.files SoundPlaybackExample.java
   */
  public String getName() {
    return this.sound.getName();
  }

  /**
   * @return whether this playback is queued or playing
   * @example.files SoundPlaybackExample.java
   */
  public boolean isPlaying() {
    return this.sound.isPlaying();
  }

  /** Stops this playback, leaving other copies alone.
   * @example.files SoundPlaybackExample.java
   */
  public void stop() {
    this.sound.stop();
  }

  /**
   * Changes only this playback's volume. Values outside 0..100 are clamped.
   * @param percent volume in percent
   * @throws IllegalArgumentException if the volume is not finite
   *
   * @example.files SoundPlaybackExample.java
   */
  public void setVolume(double percent) {
    if (!Double.isFinite(percent)) {
      throw new IllegalArgumentException("Sound volume must be a finite percentage.");
    }
    this.sound.setVolume(percent / 100.0);
  }

  /**
   * @return this playback's volume in percent
   * @example.files SoundPlaybackExample.java
   */
  public double getVolume() {
    return this.sound.getVolume() * 100;
  }

  /** Stops this playback.
   * @example.files SoundPlaybackExample.java
   */
  @Override
  public void close() {
    this.stop();
  }
}
