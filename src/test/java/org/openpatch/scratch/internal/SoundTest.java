package org.openpatch.scratch.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Decoding is tested without ever opening a {@link javax.sound.sampled.Clip},
 * because a build machine usually has no audio device.
 */
class SoundTest {

  /** An Ogg well below the decoder's block size - these used to decode to nothing. */
  private static final String SHORT_OGG = "sounds/impact/footstep_carpet_000.ogg";

  /** An Ogg above the decoder's block size, which always worked. */
  private static final String LONG_OGG = "sounds/impact/impactMining_002.ogg";

  @Test
  void shortOggFilesDecodeToAudio() throws Exception {
    assertTrue(Files.size(Path.of(Applet.getPath(SHORT_OGG))) < 8192,
        "this test is only meaningful for a file below the decoder's block size");

    assertTrue(decodedBytes(SHORT_OGG) > 0, "short ogg decoded to nothing");
  }

  @Test
  void longOggFilesStillDecodeToAudio() throws Exception {
    assertTrue(decodedBytes(LONG_OGG) > 0, "long ogg decoded to nothing");
  }

  @Test
  void oggIsDecodedToSignedPcm() throws Exception {
    try (AudioInputStream in = Sound.toPlayableStream(Sound.openStream(Applet.getPath(SHORT_OGG)))) {
      assertEquals(AudioFormat.Encoding.PCM_SIGNED, in.getFormat().getEncoding());
      assertEquals(16, in.getFormat().getSampleSizeInBits());
    }
  }

  @Test
  void recognisesTheOggMarker() throws Exception {
    assertTrue(Sound.isOgg(Files.readAllBytes(Path.of(Applet.getPath(SHORT_OGG)))));
    assertFalse(Sound.isOgg(new byte[] { 'R', 'I', 'F', 'F' }));
    assertFalse(Sound.isOgg(new byte[] { 'O', 'g' }));
  }

  @Test
  void nonOggFilesAreNotPadded() throws Exception {
    // A WAV must reach the decoder byte for byte, so its frame count has to
    // match what the header declares.
    Path wav = Files.createTempFile("scratch-test-", ".wav");
    try {
      Files.write(wav, silentWav(100));
      try (AudioInputStream in = Sound.openStream(wav.toString())) {
        assertEquals(100, in.getFrameLength());
      }
    } finally {
      Files.deleteIfExists(wav);
    }
  }

  @Test
  void readsAWholeSoundIntoMemory() throws Exception {
    try (AudioInputStream in = Sound.toPlayableStream(Sound.openStream(Applet.getPath(LONG_OGG)))) {
      assertEquals(decodedBytes(LONG_OGG), Sound.readSamples(in).length);
    }
  }

  @Test
  void keepsReadingAfterAnEmptyRead() throws Exception {
    // The Ogg decoder hands back 0 bytes now and then while it refills its
    // buffer. Taking that for the end of the stream would cut sounds short.
    byte[] audio = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
    AudioFormat format = new AudioFormat(8000, 16, 1, true, false);

    try (AudioInputStream in = new AudioInputStream(
        new StallingStream(audio), format, audio.length / format.getFrameSize())) {
      assertArrayEquals(audio, Sound.readSamples(in));
    }
  }

  @Test
  @Timeout(10)
  void givesUpOnADecoderThatNeverDelivers() throws Exception {
    // Reading on forever would hang the program while it loads its assets.
    AudioFormat format = new AudioFormat(8000, 16, 1, true, false);

    try (AudioInputStream in = new AudioInputStream(
        new SilentStream(), format, AudioSystem.NOT_SPECIFIED)) {
      assertEquals(0, Sound.readSamples(in).length);
    }
  }

  @Test
  void cutsOffAHalfFrameAtTheEnd() throws Exception {
    // A Clip can only be opened with whole frames.
    byte[] audio = new byte[] { 1, 2, 3, 4, 5 };
    AudioFormat stereo = new AudioFormat(8000, 16, 2, true, false); // 4 bytes per frame

    try (AudioInputStream in = new AudioInputStream(
        new ByteArrayInputStream(audio), stereo, AudioSystem.NOT_SPECIFIED)) {
      assertEquals(4, Sound.readSamples(in).length);
    }
  }

  /** A stream that never reads anything and never ends either. */
  private static class SilentStream extends java.io.InputStream {
    @Override
    public int read() {
      return 0;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
      return 0;
    }
  }

  /** A stream that reads nothing at first, the way the Ogg decoder does. */
  private static class StallingStream extends ByteArrayInputStream {
    private boolean stalled = false;

    StallingStream(byte[] data) {
      super(data);
    }

    @Override
    public synchronized int read(byte[] buffer, int offset, int length) {
      if (!stalled) {
        stalled = true;
        return 0;
      }
      return super.read(buffer, offset, length);
    }
  }

  private long decodedBytes(String resource) throws Exception {
    try (AudioInputStream in = Sound.toPlayableStream(Sound.openStream(Applet.getPath(resource)))) {
      byte[] buffer = new byte[4096];
      long total = 0;
      int zeroReads = 0;
      while (true) {
        int read = in.read(buffer);
        if (read == -1) {
          break;
        }
        // The Ogg decoder returns 0 now and then while it fills its buffer.
        if (read == 0) {
          if (++zeroReads > 20) {
            break;
          }
          continue;
        }
        total += read;
      }
      return total;
    }
  }

  /** Builds a minimal mono 8 kHz 16-bit PCM wav holding the given frame count. */
  private byte[] silentWav(int frames) {
    int dataSize = frames * 2;
    java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(44 + dataSize)
        .order(java.nio.ByteOrder.LITTLE_ENDIAN);
    buffer.put("RIFF".getBytes());
    buffer.putInt(36 + dataSize);
    buffer.put("WAVE".getBytes());
    buffer.put("fmt ".getBytes());
    buffer.putInt(16);
    buffer.putShort((short) 1); // PCM
    buffer.putShort((short) 1); // mono
    buffer.putInt(8000);
    buffer.putInt(16000); // byte rate
    buffer.putShort((short) 2); // block align
    buffer.putShort((short) 16); // bits per sample
    buffer.put("data".getBytes());
    buffer.putInt(dataSize);
    return buffer.array();
  }
}
