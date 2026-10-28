package org.openpatch.scratch.desktop;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;
import org.openpatch.scratch.internal.platform.AudioDevice;

/** OpenAL Soft's default device: buffers and sources as OpenAL has them. */
final class OpenAlDevice implements AudioDevice {

  private final long device;
  private final long context;

  private OpenAlDevice(long device, long context) {
    this.device = device;
    this.context = context;
  }

  /** The default device, or null when the machine has none. */
  static OpenAlDevice open() {
    long device = 0, context = 0;
    try {
      device = alcOpenDevice((ByteBuffer) null);
      if (device == 0) {
        return null;
      }
      ALCCapabilities alc = ALC.createCapabilities(device);
      context = alcCreateContext(device, (IntBuffer) null);
      alcMakeContextCurrent(context);
      AL.createCapabilities(alc);
      return new OpenAlDevice(device, context);
    } catch (Throwable t) {
      // a machine that cannot open a device must not take the library down: go silent instead
      try {
        if (context != 0) {
          alcMakeContextCurrent(0L);
          alcDestroyContext(context);
        }
        if (device != 0) {
          alcCloseDevice(device);
        }
      } catch (Throwable ignored) {
        // teardown is best effort
      }
      return null;
    }
  }

  @Override
  public int createBuffer(byte[] samples, int channels, int bits, int sampleRate) {
    int format = channels == 1
        ? (bits == 8 ? AL_FORMAT_MONO8 : AL_FORMAT_MONO16)
        : (bits == 8 ? AL_FORMAT_STEREO8 : AL_FORMAT_STEREO16);
    // alBufferData copies on upload, so a scratch direct buffer is enough and ours to free
    ByteBuffer direct = MemoryUtil.memAlloc(samples.length).put(samples).flip();
    int buffer = alGenBuffers();
    if (bits == 8) {
      alBufferData(buffer, format, direct, sampleRate);
    } else {
      alBufferData(buffer, format, direct.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer(), sampleRate);
    }
    MemoryUtil.memFree(direct);
    return buffer;
  }

  @Override
  public void deleteBuffer(int buffer) {
    alDeleteBuffers(buffer);
  }

  @Override
  public int createSource() {
    return alGenSources();
  }

  @Override
  public void deleteSource(int source) {
    alDeleteSources(source);
  }

  @Override
  public void play(int source, int buffer, float gain, boolean loop) {
    alSourcei(source, AL_BUFFER, buffer);
    alSourcef(source, AL_GAIN, gain);
    alSourcei(source, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
    alSourcePlay(source);
  }

  @Override
  public void stop(int source) {
    alSourceStop(source);
  }

  @Override
  public boolean isPlaying(int source) {
    int[] state = new int[1];
    alGetSourcei(source, AL_SOURCE_STATE, state);
    return state[0] == AL_PLAYING;
  }

  @Override
  public void setGain(int source, float gain) {
    alSourcef(source, AL_GAIN, gain);
  }

  @Override
  public void close() {
    try {
      alcMakeContextCurrent(0L);
      alcDestroyContext(this.context);
    } catch (Throwable ignored) {
      // teardown is best effort
    }
    alcCloseDevice(this.device);
  }
}
