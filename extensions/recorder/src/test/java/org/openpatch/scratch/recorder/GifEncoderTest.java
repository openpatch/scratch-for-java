package org.openpatch.scratch.recorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link GifEncoder}. The javax.imageio GIF reader is used here in the tests only; the
 * encoder itself is free of java.desktop.
 */
class GifEncoderTest {

  private static final int RED = 0xFFFF0000;
  private static final int GREEN = 0xFF00FF00;
  private static final int BLUE = 0xFF0000FF;

  private static int[] solid(int width, int height, int argb) {
    int[] pixels = new int[width * height];
    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = argb;
    }
    return pixels;
  }

  private static byte[] encodeThreeColorFrames() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GifEncoder encoder = new GifEncoder();
    assertTrue(encoder.start(out));
    encoder.setDelay(100);
    encoder.addFrame(solid(8, 6, RED), 8, 6);
    encoder.addFrame(solid(8, 6, GREEN), 8, 6);
    encoder.addFrame(solid(8, 6, BLUE), 8, 6);
    assertTrue(encoder.finish());
    return out.toByteArray();
  }

  @Test
  void writesValidGifStructure() {
    byte[] gif = encodeThreeColorFrames();

    // header
    assertEquals("GIF89a", new String(gif, 0, 6));

    // logical screen size, little-endian at offsets 6 and 8
    assertEquals(8, gif[6] & 0xFF);
    assertEquals(0, gif[7] & 0xFF);
    assertEquals(6, gif[8] & 0xFF);
    assertEquals(0, gif[9] & 0xFF);

    // trailer
    assertEquals(0x3B, gif[gif.length - 1] & 0xFF);
  }

  @Test
  void roundTripsFrameColors() throws IOException {
    byte[] gif = encodeThreeColorFrames();
    int[] expected = {
      (RED >> 16) & 0xFF, (RED >> 8) & 0xFF, RED & 0xFF,
      (GREEN >> 16) & 0xFF, (GREEN >> 8) & 0xFF, GREEN & 0xFF,
      (BLUE >> 16) & 0xFF, (BLUE >> 8) & 0xFF, BLUE & 0xFF
    };

    try (InputStream in = new ByteArrayInputStream(gif);
        ImageInputStream iis = ImageIO.createImageInputStream(in)) {
      Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
      assertTrue(readers.hasNext(), "no GIF reader available");
      ImageReader reader = readers.next();
      reader.setInput(iis, false);
      assertEquals(3, reader.getNumImages(true));
      for (int i = 0; i < 3; i++) {
        java.awt.image.BufferedImage frame = reader.read(i);
        assertEquals(8, frame.getWidth());
        assertEquals(6, frame.getHeight());
        int centre = frame.getRGB(frame.getWidth() / 2, frame.getHeight() / 2);
        for (int c = 0; c < 3; c++) {
          int channel = (centre >> (16 - 8 * c)) & 0xFF;
          assertTrue(
              Math.abs(channel - expected[i * 3 + c]) <= 16,
              "frame " + i + " channel " + c + ": " + channel + " vs " + expected[i * 3 + c]);
        }
      }
    }
  }

  @Test
  void scalesLaterFramesToFirstFrameSize() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GifEncoder encoder = new GifEncoder();
    assertTrue(encoder.start(out));
    assertTrue(encoder.addFrame(solid(8, 6, RED), 8, 6));
    assertTrue(encoder.addFrame(solid(16, 12, GREEN), 16, 12));
    assertTrue(encoder.finish());
  }

  @Test
  void startWritesFile(@TempDir Path tempDir) throws IOException {
    Path file = tempDir.resolve("frames.gif");
    GifEncoder encoder = new GifEncoder();
    assertTrue(encoder.start(file.toString()));
    encoder.addFrame(solid(8, 6, RED), 8, 6);
    assertTrue(encoder.finish());

    byte[] gif = Files.readAllBytes(file);
    assertEquals("GIF89a", new String(gif, 0, 6));
    assertEquals(0x3B, gif[gif.length - 1] & 0xFF);
  }

  @Test
  void addFrameBeforeStartFails() {
    GifEncoder encoder = new GifEncoder();
    assertFalse(encoder.addFrame(solid(8, 6, RED), 8, 6));
  }
}
