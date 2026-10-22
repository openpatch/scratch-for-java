package org.openpatch.scratch.internal.audio;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Decoding only, no audio device needed. */
class SoundDataTest {

  @Test
  void decodes16BitStereo() throws IOException {
    byte[] samples = new byte[4800]; // 1200 frames, 2 channels, 2 bytes each
    for (int i = 0; i < samples.length; i++) {
      samples[i] = (byte) (i * 31);
    }
    SoundData data = SoundData.decode(wav(1, 2, 48000, 16, samples), "stereo.wav");
    assertEquals(2, data.channels());
    assertEquals(16, data.bits());
    assertEquals(48000, data.sampleRate());
    assertEquals(samples.length / (2 * 2 * 48000.0), data.seconds(), 1e-9);
    assertArrayEquals(samples, data.pcm());
  }

  @Test
  void decodes8BitMono() throws IOException {
    byte[] samples = new byte[2205]; // a tenth of a second
    SoundData data = SoundData.decode(wav(1, 1, 22050, 8, samples), "mono.wav");
    assertEquals(1, data.channels());
    assertEquals(8, data.bits());
    assertEquals(22050, data.sampleRate());
    assertEquals(samples.length / (1 * 22050 * 1.0), data.seconds(), 1e-9);
    assertArrayEquals(samples, data.pcm());
  }

  @Test
  void skipsUnknownChunks() throws IOException {
    byte[] samples = new byte[16];
    ByteArrayOutputStream body = new ByteArrayOutputStream();
    // odd body: the parser must skip the 3 bytes and the pad byte behind them
    writeChunk(body, "LIST", new byte[3]);
    writeChunk(body, "fmt ", fmt(1, 1, 44100, 16));
    writeChunk(body, "data", samples);
    SoundData data = SoundData.decode(riff(body.toByteArray()), "with-list.wav");
    assertEquals(1, data.channels());
    assertEquals(16, data.bits());
    assertEquals(44100, data.sampleRate());
    assertArrayEquals(samples, data.pcm());
  }

  @Test
  void rejectsFloatWav() {
    byte[] samples = new byte[8];
    IOException e = assertThrows(IOException.class,
        () -> SoundData.decode(wav(3, 2, 48000, 16, samples), "float.wav"));
    assertTrue(e.getMessage().contains("float.wav"), e.getMessage());
  }

  @Test
  void rejectsUnknownFormat() {
    IOException e = assertThrows(IOException.class,
        () -> SoundData.decode(new byte[] {1, 2, 3, 4, 5}, "bogus.txt"));
    assertTrue(e.getMessage().contains("bogus.txt"), e.getMessage());
    assertTrue(e.getMessage().contains("only WAV (PCM) and OGG sounds are supported"), e.getMessage());
  }

  @Test
  void decodesEveryShippedOgg() throws IOException {
    List<Path> oggs = new ArrayList<>();
    try (Stream<Path> paths = Files.walk(Path.of("src/main/resources/sounds"))) {
      paths.filter(path -> path.toString().endsWith(".ogg")).forEach(oggs::add);
    }
    assertTrue(oggs.size() >= 100, "expected at least 100 built-in sounds, found " + oggs.size());
    for (Path ogg : oggs) {
      SoundData data = SoundData.decode(Files.readAllBytes(ogg), ogg.toString());
      assertTrue(data.channels() == 1 || data.channels() == 2, ogg + ": channels " + data.channels());
      assertTrue(data.sampleRate() > 0, ogg + ": sample rate " + data.sampleRate());
      assertTrue(data.seconds() > 0, ogg + ": seconds " + data.seconds());
    }
  }

  @Test
  void decodesBumpWav() throws IOException {
    Path bump = Path.of("../examples/src/main/java/reference/assets/bump.wav");
    SoundData data = SoundData.decode(Files.readAllBytes(bump), bump.toString());
    assertEquals(16, data.bits());
    assertEquals(2, data.channels());
    assertEquals(48000, data.sampleRate());
  }

  // ---- WAV builders -----------------------------------------------------------

  /** A minimal RIFF/WAVE file: a fmt chunk and a data chunk around {@code samples}. */
  private static byte[] wav(int formatTag, int channels, int rate, int bits, byte[] samples) {
    ByteArrayOutputStream body = new ByteArrayOutputStream();
    writeChunk(body, "fmt ", fmt(formatTag, channels, rate, bits));
    writeChunk(body, "data", samples);
    return riff(body.toByteArray());
  }

  private static byte[] riff(byte[] body) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.writeBytes("RIFF".getBytes(StandardCharsets.US_ASCII));
    le32(out, 4 + body.length); // "WAVE" plus every chunk
    out.writeBytes("WAVE".getBytes(StandardCharsets.US_ASCII));
    out.writeBytes(body);
    return out.toByteArray();
  }

  private static void writeChunk(ByteArrayOutputStream out, String id, byte[] chunkBody) {
    out.writeBytes(id.getBytes(StandardCharsets.US_ASCII));
    le32(out, chunkBody.length);
    out.writeBytes(chunkBody);
    if (chunkBody.length % 2 == 1) {
      out.write(0); // chunks are word aligned, as the parser expects
    }
  }

  private static byte[] fmt(int formatTag, int channels, int rate, int bits) {
    ByteArrayOutputStream fmt = new ByteArrayOutputStream();
    le16(fmt, formatTag);
    le16(fmt, channels);
    le32(fmt, rate);
    le32(fmt, rate * channels * bits / 8); // byte rate
    le16(fmt, channels * bits / 8); // block align
    le16(fmt, bits);
    return fmt.toByteArray();
  }

  private static void le16(ByteArrayOutputStream out, int value) {
    out.write(value & 0xFF);
    out.write((value >> 8) & 0xFF);
  }

  private static void le32(ByteArrayOutputStream out, int value) {
    for (int i = 0; i < 4; i++) {
      out.write((value >> (8 * i)) & 0xFF);
    }
  }
}
