package org.openpatch.scratch.internal;

import java.util.ArrayList;
import java.util.List;
import org.openpatch.scratch.extensions.sound.SoundPlayback;

/** Copies belong to the sprite or stage that started them, not to the asset cache. */
public final class SoundPlaybacks {
  private final List<SoundPlayback> playing = new ArrayList<>();

  public synchronized SoundPlayback play(Sound source, double volume) {
    this.playing.removeIf(playback -> !playback.isPlaying());
    SoundPlayback playback = SoundPlayback.start(source, volume);
    this.playing.add(playback);
    return playback;
  }

  public synchronized boolean isPlaying(String name) {
    this.playing.removeIf(playback -> !playback.isPlaying());
    return this.playing.stream().anyMatch(playback -> playback.getName().equals(name));
  }

  public synchronized void stop(String name) {
    this.playing.removeIf(playback -> {
      if (playback.getName().equals(name)) {
        playback.stop();
        return true;
      }
      return !playback.isPlaying();
    });
  }

  public synchronized void stopAll() {
    this.playing.forEach(SoundPlayback::stop);
    this.playing.clear();
  }
}
