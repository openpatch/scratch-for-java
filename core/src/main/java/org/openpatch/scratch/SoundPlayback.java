package org.openpatch.scratch;

import org.openpatch.scratch.internal.audio.AudioEngine;

/**
 * One copy of a sound started with {@code playSoundCopy}: it can be stopped or made louder or
 * quieter while it plays, independently of other copies.
 */
public final class SoundPlayback {

  private final int handle;

  SoundPlayback(int handle) {
    this.handle = handle;
  }

  /**
   * Stops this copy.
   *
   * @example.files SoundPlaybackExample.java
   */
  public void stop() {
    AudioEngine.get().stop(this.handle);
  }

  /**
   * @return whether this copy still plays
   *
   * @example.files SoundPlaybackExample.java
   */
  public boolean isPlaying() {
    return AudioEngine.get().isPlaying(this.handle);
  }

  /**
   * @param volume from 0 to 100
   *
   * @example.files SoundPlaybackExample.java
   */
  public void setVolume(double volume) {
    AudioEngine.get().setVolume(this.handle, Math.max(0, Math.min(100, volume)) / 100);
  }
}
