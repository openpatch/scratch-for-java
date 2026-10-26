package org.openpatch.scratch.internal.audio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryUtil;
import org.openpatch.scratch.internal.Natives;
import org.openpatch.scratch.internal.image.Images;

/**
 * A sound decoded to raw PCM, ready for OpenAL: 8- or 16-bit, mono or stereo, little-endian as
 * WAV stores it (8-bit unsigned, 16-bit signed).
 */
public final class SoundData {

  private final int channels;
  private final int sampleRate;
  private final int bits;
  private final byte[] pcm;
  private final double seconds;

  private SoundData(int channels, int sampleRate, int bits, byte[] pcm) {
    this.channels = channels;
    this.sampleRate = sampleRate;
    this.bits = bits;
    this.pcm = pcm;
    this.seconds = pcm.length / (channels * sampleRate * (bits / 8.0));
  }

  /** The channel count: 1 for mono, 2 for stereo. */
  public int channels() {
    return this.channels;
  }

  /** The sample rate in hertz. */
  public int sampleRate() {
    return this.sampleRate;
  }

  /** The bits per sample: 8 (unsigned) or 16 (signed). */
  public int bits() {
    return this.bits;
  }

  /** The decoded PCM. Callers must not modify the returned array. */
  public byte[] pcm() {
    return this.pcm;
  }

  /** How long the sound plays, in seconds. */
  public double seconds() {
    return this.seconds;
  }

  /**
   * Decodes the bytes of a sound file, a PCM WAV or an OGG.
   *
   * @param file the bytes of the whole file
   * @param name the path or label of the sound, used in error messages
   * @throws IOException when the bytes are neither a PCM WAV nor an OGG, or malformed
   */
  public static SoundData decode(byte[] file, String name) throws IOException {
    if (file.length >= 12 && startsWith(file, 0, "RIFF") && startsWith(file, 8, "WAVE")) {
      return decodeWav(file, name);
    }
    if (file.length >= 4 && startsWith(file, 0, "OggS")) {
      return decodeOgg(file, name);
    }
    throw new IOException(name + ": only WAV (PCM) and OGG sounds are supported");
  }

  /**
   * Reads and decodes the sound at {@code path}: a file relative to the working directory, else a
   * resource on the classpath.
   *
   * @throws IllegalArgumentException when there is no such sound or it cannot be decoded
   */
  public static SoundData load(String path) {
    // a built-in sound by name ("click"), as a sprite costume can be a built-in sprite
    if (org.openpatch.scratch.internal.BuiltinAssets.isBuiltinName(path)) {
      String resource = org.openpatch.scratch.internal.BuiltinSounds.get(path);
      if (resource != null) {
        path = resource;
      }
    }
    byte[] bytes = Images.read(path);
    if (bytes == null) {
      throw new IllegalArgumentException("Sound not found: " + path);
    }
    try {
      return decode(bytes, path);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  private static boolean startsWith(byte[] file, int offset, String magic) {
    for (int i = 0; i < magic.length(); i++) {
      if (file[offset + i] != magic.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  /** RIFF/WAVE by hand: PCM only, the one variant Scratch projects ship. */
  private static SoundData decodeWav(byte[] file, String name) throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(file).order(ByteOrder.LITTLE_ENDIAN);
    buffer.position(12); // behind "RIFF", the size and "WAVE"
    int formatTag = 0;
    int channels = 0;
    int rate = 0;
    int bits = 0;
    byte[] samples = null;
    while (buffer.remaining() >= 8) {
      int id = buffer.getInt();
      int size = buffer.getInt();
      // chunks are word aligned: an odd body carries a pad byte before the next header
      long end = (long) buffer.position() + (size & 0xFFFFFFFFL) + (size & 1);
      int next = (int) Math.min(end, buffer.limit());
      if (id == 0x20746D66 /* "fmt " */ && size >= 16 && buffer.remaining() >= 16) {
        formatTag = buffer.getShort() & 0xFFFF;
        channels = buffer.getShort() & 0xFFFF;
        rate = buffer.getInt();
        buffer.getInt(); // byte rate, follows from the rest
        buffer.getShort(); // block align
        bits = buffer.getShort() & 0xFFFF;
        if (formatTag == 0xFFFE /* WAVE_FORMAT_EXTENSIBLE */ && size >= 26 && buffer.remaining() >= 10) {
          // the real format tag hides in the sub-format GUID, behind the extra fields
          buffer.getShort(); // cbSize
          buffer.getShort(); // valid bits per sample
          buffer.getInt(); // channel mask
          formatTag = buffer.getShort() & 0xFFFF;
        }
      } else if (id == 0x61746164 /* "data" */) {
        int length = (int) Math.min(size & 0xFFFFFFFFL, buffer.remaining());
        samples = new byte[length];
        buffer.get(samples);
      }
      buffer.position(next); // skips unknown chunks and their pad byte
    }
    if (formatTag != 1 /* PCM */ || (bits != 8 && bits != 16) || (channels != 1 && channels != 2)
        || rate <= 0 || samples == null) {
      throw new IOException(name + " is not an 8- or 16-bit PCM mono/stereo WAV");
    }
    return new SoundData(channels, rate, bits, samples);
  }

  private static SoundData decodeOgg(byte[] file, String name) throws IOException {
    Natives.init(); // loads the native libraries before the first LWJGL call
    ByteBuffer encoded = MemoryUtil.memAlloc(file.length).put(file).flip();
    IntBuffer channels = BufferUtils.createIntBuffer(1);
    IntBuffer rate = BufferUtils.createIntBuffer(1);
    ShortBuffer pcm = STBVorbis.stb_vorbis_decode_memory(encoded, channels, rate);
    MemoryUtil.memFree(encoded); // the decode buffer is ours
    if (pcm == null) {
      throw new IOException("stb_vorbis could not decode " + name);
    }
    int count = pcm.remaining();
    byte[] bytes = new byte[count * 2];
    for (int i = 0; i < count; i++) {
      short sample = pcm.get();
      // OpenAL and WAV agree on little-endian: the low byte comes first
      bytes[2 * i] = (byte) sample;
      bytes[2 * i + 1] = (byte) (sample >> 8);
    }
    MemoryUtil.memFree(pcm); // the decode buffer is ours
    int decodedChannels = channels.get(0);
    if (decodedChannels != 1 && decodedChannels != 2) {
      throw new IOException(name + ": only mono and stereo sounds are supported");
    }
    return new SoundData(decodedChannels, rate.get(0), 16, bytes);
  }
}
