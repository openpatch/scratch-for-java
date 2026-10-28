package org.openpatch.scratch.internal.audio;

import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.openpatch.scratch.internal.platform.AudioDevice;
import org.openpatch.scratch.internal.platform.Platform;

/**
 * The one sound output of the process (the platform's {@link AudioDevice}): every sound uploads once into a buffer and plays on a
 * pool of sources, so the same sound can ring out many times at once.
 */
public final class AudioEngine {

  private static final int POOL = 32;

  private static AudioEngine instance;

  private final boolean silent;
  private final AudioDevice device;
  private final int[] sources = new int[POOL];
  private final long[] started = new long[POOL]; // when each source last started
  private final double[] volumes = new double[POOL]; // the per-sound gain, master volume on top
  private final int[] handles = new int[POOL]; // the live handle of each source, 0 when none
  private final Map<SoundData, Integer> buffers = new IdentityHashMap<>(); // identity: a sound uploads once
  private final Map<Integer, Integer> slots = new HashMap<>(); // handle -> pool slot
  private double masterVolume = 1.0;
  private boolean closed;
  private int nextHandle;

  /**
   * The one engine of the process, created on first use. On a machine without an audio device the
   * engine is silent: every method does nothing or returns a default, and never throws.
   */
  public static synchronized AudioEngine get() {
    if (AudioEngine.instance == null) {
      AudioEngine.instance = new AudioEngine();
    }
    return AudioEngine.instance;
  }

  private AudioEngine() {
    AudioDevice device = null;
    try {
      device = Platform.get().openAudio();
    } catch (Throwable t) {
      // a machine that cannot open a device must not take the library down: go silent instead
      device = null;
    }
    this.device = device;
    this.silent = device == null;
    if (this.silent) {
      // the one and only reaction to a machine without audio: stay silent, never throw
      System.err.println("Scratch for Java: no audio device, sounds stay silent.");
    }
  }

  /**
   * Uploads {@code data} and returns the buffer id, 0 on a silent engine. The same sound data
   * uploads once, whatever it is asked for.
   */
  public synchronized int buffer(SoundData data) {
    if (this.silent || this.closed || data == null) {
      return 0;
    }
    Integer cached = this.buffers.get(data);
    if (cached != null) {
      return cached;
    }
    // 8-bit is unsigned and 16-bit signed little-endian in WAV and on every device: as is
    int buffer = this.device.createBuffer(data.pcm(), data.channels(), data.bits(), data.sampleRate());
    this.buffers.put(data, buffer);
    return buffer;
  }

  /**
   * Plays {@code buffer} on a stopped source, or the one that started longest ago. The returned
   * handle stays valid until its source is reused or stopped; a silent engine returns 0.
   */
  public synchronized int play(int buffer, double volume, boolean loop) {
    if (this.silent || this.closed || buffer == 0) {
      return 0;
    }
    this.createPool();
    int pick = 0;
    for (int i = 0; i < POOL; i++) {
      if (!this.device.isPlaying(this.sources[i])) {
        pick = i; // a free source is always better than stealing one
        break;
      }
      if (this.started[i] < this.started[pick]) {
        pick = i; // all busy: steal the one that started longest ago
      }
    }
    int handle = ++this.nextHandle;
    if (this.handles[pick] != 0) {
      this.slots.remove(this.handles[pick]); // the reused source ends the old handle
    }
    this.handles[pick] = handle;
    this.slots.put(handle, pick);
    this.volumes[pick] = clamp(volume);
    int source = this.sources[pick];
    // a reused source keeps no settings
    this.device.play(source, buffer, (float) (this.volumes[pick] * this.masterVolume), loop);
    this.started[pick] = System.nanoTime();
    return handle;
  }

  /** Stops the sound behind {@code handle}, if it still plays. */
  public synchronized void stop(int handle) {
    Integer slot = this.slots.remove(handle);
    if (slot == null || this.silent || this.closed) {
      return;
    }
    this.device.stop(this.sources[slot]);
    this.handles[slot] = 0;
  }

  /** Whether the sound behind {@code handle} still plays; a reused or stopped handle does not. */
  public synchronized boolean isPlaying(int handle) {
    Integer slot = this.slots.get(handle);
    if (slot == null || this.silent || this.closed) {
      return false;
    }
    return this.device.isPlaying(this.sources[slot]);
  }

  /** Sets the gain of the sound behind {@code handle}, 0..1, master volume on top. */
  public synchronized void setVolume(int handle, double volume) {
    Integer slot = this.slots.get(handle);
    if (slot == null || this.silent || this.closed) {
      return;
    }
    this.volumes[slot] = clamp(volume);
    this.device.setGain(this.sources[slot], (float) (this.volumes[slot] * this.masterVolume));
  }

  /** Stops every sound, handles included. */
  public synchronized void stopAll() {
    if (this.silent || this.closed) {
      return;
    }
    for (int i = 0; i < POOL; i++) {
      if (this.sources[i] != 0) {
        this.device.stop(this.sources[i]);
      }
    }
    this.slots.clear();
    Arrays.fill(this.handles, 0);
  }

  /** Sets the master volume, 0..1, for the sounds playing now and the ones to come. */
  public synchronized void setMasterVolume(double volume) {
    this.masterVolume = clamp(volume);
    if (this.silent || this.closed) {
      return;
    }
    for (int i = 0; i < POOL; i++) {
      if (this.sources[i] != 0) {
        this.device.setGain(this.sources[i], (float) (this.volumes[i] * this.masterVolume));
      }
    }
  }

  /** The master volume, 0..1. */
  public synchronized double getMasterVolume() {
    return this.masterVolume;
  }

  /** Whether this machine has no audio device and the engine stays silent. */
  public synchronized boolean isSilent() {
    return this.silent;
  }

  /** Stops and deletes sources and buffers, destroys the context and closes the device. */
  public synchronized void close() {
    if (this.closed) {
      return;
    }
    this.closed = true;
    this.slots.clear();
    Arrays.fill(this.handles, 0);
    if (this.silent) {
      return;
    }
    for (int source : this.sources) {
      if (source != 0) {
        this.device.stop(source);
        this.device.deleteSource(source);
      }
    }
    for (int buffer : this.buffers.values()) {
      this.device.deleteBuffer(buffer);
    }
    this.device.close();
  }

  private void createPool() {
    if (this.sources[0] != 0) {
      return;
    }
    for (int i = 0; i < POOL; i++) {
      this.sources[i] = this.device.createSource();
    }
  }

  private static double clamp(double volume) {
    return Math.max(0.0, Math.min(1.0, volume));
  }
}
