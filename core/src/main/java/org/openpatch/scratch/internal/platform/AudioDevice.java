package org.openpatch.scratch.internal.platform;

/**
 * A sound output: buffers of samples, sources that play them. OpenAL on the desktop, Web Audio in
 * the browser. Handles are ints; 0 is none.
 */
public interface AudioDevice {

  /**
   * Uploads samples: 8-bit unsigned or 16-bit signed little-endian, interleaved for stereo.
   *
   * @return the buffer
   */
  int createBuffer(byte[] samples, int channels, int bits, int sampleRate);

  void deleteBuffer(int buffer);

  int createSource();

  void deleteSource(int source);

  /** Plays {@code buffer} on {@code source} from the start, stopping what it played. */
  void play(int source, int buffer, float gain, boolean loop);

  void stop(int source);

  boolean isPlaying(int source);

  void setGain(int source, float gain);

  /** Stops everything and lets go of the device. */
  void close();
}
