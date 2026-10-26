package org.openpatch.scratch.internal.audio;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;
import org.openpatch.scratch.internal.Natives;

/**
 * The one OpenAL connection of the process: every sound uploads once into a buffer and plays on a
 * pool of sources, so the same sound can ring out many times at once.
 */
public final class AudioEngine {

  private static final int POOL = 32;

  private static AudioEngine instance;

  private final boolean silent;
  private long device;
  private long context;
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
    long device = 0;
    boolean silent = true;
    try {
      Natives.init(); // LWJGL's memory backend must be chosen before its first call
      device = alcOpenDevice((ByteBuffer) null);
      if (device != 0) {
        ALCCapabilities alc = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        alcMakeContextCurrent(this.context);
        AL.createCapabilities(alc);
        silent = false;
      }
    } catch (Throwable t) {
      // a machine that cannot open a device must not take the library down: go silent instead
      this.destroyContext();
      if (device != 0) {
        try {
          alcCloseDevice(device);
        } catch (Throwable ignored) {
          // teardown is best effort
        }
        device = 0;
      }
    }
    this.device = device;
    this.silent = silent;
    if (silent) {
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
    int format = data.channels() == 1
        ? (data.bits() == 8 ? AL_FORMAT_MONO8 : AL_FORMAT_MONO16)
        : (data.bits() == 8 ? AL_FORMAT_STEREO8 : AL_FORMAT_STEREO16);
    byte[] pcm = data.pcm();
    // alBufferData copies on upload, so a scratch direct buffer is enough and ours to free
    ByteBuffer direct = MemoryUtil.memAlloc(pcm.length).put(pcm).flip();
    int buffer = alGenBuffers();
    if (data.bits() == 8) {
      // 8-bit is unsigned in both WAV and OpenAL, 16-bit signed LE likewise: copy as is
      alBufferData(buffer, format, direct, data.sampleRate());
    } else {
      alBufferData(buffer, format, direct.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer(), data.sampleRate());
    }
    MemoryUtil.memFree(direct);
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
      if (this.state(this.sources[i]) != AL_PLAYING) {
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
    alSourcei(source, AL_BUFFER, buffer);
    alSourcef(source, AL_GAIN, (float) (this.volumes[pick] * this.masterVolume));
    alSourcei(source, AL_LOOPING, loop ? AL_TRUE : AL_FALSE); // a reused source keeps no settings
    alSourcePlay(source);
    this.started[pick] = System.nanoTime();
    return handle;
  }

  /** Stops the sound behind {@code handle}, if it still plays. */
  public synchronized void stop(int handle) {
    Integer slot = this.slots.remove(handle);
    if (slot == null || this.silent || this.closed) {
      return;
    }
    alSourceStop(this.sources[slot]);
    this.handles[slot] = 0;
  }

  /** Whether the sound behind {@code handle} still plays; a reused or stopped handle does not. */
  public synchronized boolean isPlaying(int handle) {
    Integer slot = this.slots.get(handle);
    if (slot == null || this.silent || this.closed) {
      return false;
    }
    return this.state(this.sources[slot]) == AL_PLAYING;
  }

  /** Sets the gain of the sound behind {@code handle}, 0..1, master volume on top. */
  public synchronized void setVolume(int handle, double volume) {
    Integer slot = this.slots.get(handle);
    if (slot == null || this.silent || this.closed) {
      return;
    }
    this.volumes[slot] = clamp(volume);
    alSourcef(this.sources[slot], AL_GAIN, (float) (this.volumes[slot] * this.masterVolume));
  }

  /** Stops every sound, handles included. */
  public synchronized void stopAll() {
    if (this.silent || this.closed) {
      return;
    }
    for (int i = 0; i < POOL; i++) {
      if (this.sources[i] != 0) {
        alSourceStop(this.sources[i]);
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
        alSourcef(this.sources[i], AL_GAIN, (float) (this.volumes[i] * this.masterVolume));
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
        alSourceStop(source);
        alDeleteSources(source);
      }
    }
    for (int buffer : this.buffers.values()) {
      alDeleteBuffers(buffer);
    }
    this.destroyContext();
    if (this.device != 0) {
      alcCloseDevice(this.device);
      this.device = 0;
    }
  }

  private void createPool() {
    if (this.sources[0] != 0) {
      return;
    }
    for (int i = 0; i < POOL; i++) {
      this.sources[i] = alGenSources();
    }
  }

  private int state(int source) {
    int[] state = new int[1];
    alGetSourcei(source, AL_SOURCE_STATE, state);
    return state[0];
  }

  private void destroyContext() {
    if (this.context == 0) {
      return;
    }
    try {
      alcMakeContextCurrent(0L);
      alcDestroyContext(this.context);
    } catch (Throwable ignored) {
      // teardown is best effort
    }
    this.context = 0;
  }

  private static double clamp(double volume) {
    return Math.max(0.0, Math.min(1.0, volume));
  }
}
