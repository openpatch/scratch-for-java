package org.openpatch.scratch.internal;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.openpatch.scratch.internal.audio.AudioEngine;
import org.openpatch.scratch.internal.audio.SoundData;

/**
 * The sounds of a sprite or stage, by name: each decoded once (shared by path across the
 * program), the "main" playback per name for {@code playSound}, and the object's volume.
 */
public final class SoundBank {

  private static final Map<String, SoundData> DECODED = new ConcurrentHashMap<>();

  private final Map<String, SoundData> sounds = new LinkedHashMap<>();
  private final Map<String, Integer> playing = new LinkedHashMap<>();
  private final Set<Integer> copies = new HashSet<>();
  private final Set<String> warned = new HashSet<>();
  private double volume = 100;

  /** A bank for a clone: the same sounds and volume, nothing playing. */
  public SoundBank copy() {
    SoundBank copy = new SoundBank();
    copy.sounds.putAll(this.sounds);
    copy.volume = this.volume;
    return copy;
  }

  /** Decodes a WAV or OGG file into the cache {@link #add} reads (the assets preloader). */
  public static void preload(String path) {
    DECODED.computeIfAbsent(path, SoundData::load);
  }

  /** Decodes a WAV or OGG file (once per path in the program) and names it. */
  public void add(String name, String path) {
    this.sounds.put(name, DECODED.computeIfAbsent(path, p -> {
      SoundData data = SoundData.load(p);
      Assets.noteLoadedLate("sound", p);
      return data;
    }));
  }

  /** Starts a sound unless it is playing already. */
  public void play(String name) {
    SoundData data = this.find(name);
    if (data == null) {
      return;
    }
    Integer handle = this.playing.get(name);
    AudioEngine engine = AudioEngine.get();
    if (handle != null && engine.isPlaying(handle)) {
      return;
    }
    this.playing.put(name, engine.play(engine.buffer(data), this.volume / 100, false));
  }

  /** Starts another copy of a sound, at a volume of its own (0–100); returns its handle. */
  public int playCopy(String name, double volume) {
    SoundData data = this.find(name);
    if (data == null) {
      return 0;
    }
    AudioEngine engine = AudioEngine.get();
    int handle = engine.play(engine.buffer(data), clamp(volume) / 100 * this.volume / 100, false);
    this.copies.add(handle);
    return handle;
  }

  public boolean isPlaying(String name) {
    Integer handle = this.playing.get(name);
    return handle != null && AudioEngine.get().isPlaying(handle);
  }

  public void stop(String name) {
    Integer handle = this.playing.remove(name);
    if (handle != null) {
      AudioEngine.get().stop(handle);
    }
  }

  /** Stops this object's sounds, copies included; other objects' sounds play on. */
  public void stopAll() {
    AudioEngine engine = AudioEngine.get();
    for (int handle : this.playing.values()) {
      engine.stop(handle);
    }
    for (int handle : this.copies) {
      engine.stop(handle);
    }
    this.playing.clear();
    this.copies.clear();
  }

  public double getVolume() {
    return this.volume;
  }

  /** Scratch's volume, 0–100, for the sounds playing now and the ones to come. */
  public void setVolume(double volume) {
    this.volume = clamp(volume);
    AudioEngine engine = AudioEngine.get();
    for (int handle : this.playing.values()) {
      engine.setVolume(handle, this.volume / 100);
    }
  }

  private SoundData find(String name) {
    SoundData data = this.sounds.get(name);
    if (data == null && this.warned.add(name)) {
      System.err.println("Scratch for Java: there is no sound called \"" + name + "\". "
          + (this.sounds.isEmpty() ? "Add it first with addSound." : "Sounds: " + this.sounds.keySet()));
    }
    return data;
  }

  private static double clamp(double v) {
    return Math.max(0, Math.min(100, v));
  }
}
