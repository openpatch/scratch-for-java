package org.openpatch.scratch.internal.image;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ImageTest {

  @Test
  void aPngWrittenAndReadKeepsEveryPixelIncludingAlpha(@TempDir Path dir) throws IOException {
    Image image = new Image(3, 2, new int[] {0xff112233, 0x80445566, 0x00000000, 0xffffffff, 0x01020304, 0xff000000});
    Path file = dir.resolve("out/test.png");
    image.writePng(file);
    Image back = Image.decode(Files.readAllBytes(file), "test.png");
    assertEquals(3, back.getWidth());
    assertArrayEquals(image.getArgb(), back.getArgb());
  }

  @Test
  void regionsAndMirrors() {
    Image image = new Image(2, 2, new int[] {1, 2, 3, 4});
    assertArrayEquals(new int[] {2, 4}, image.region(1, 0, 1, 2).getArgb());
    assertArrayEquals(new int[] {2, 1, 4, 3}, image.mirrorX().getArgb());
    assertArrayEquals(new int[] {4, 0}, image.region(1, 1, 2, 1).getArgb(), "outside is transparent");
  }

  @Test
  void garbageIsReportedWithAReason() {
    IOException e = assertThrows(IOException.class, () -> Image.decode(new byte[] {1, 2, 3}, "garbage.png"));
    assertTrue(e.getMessage().startsWith("Could not decode garbage.png: "), e.getMessage());
  }

  @Test
  void imagesAreLoadedOnceAndMissingOnesNamed() {
    assertSame(Images.load("costumes/red10.png"), Images.load("costumes/red10.png"));
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Images.load("nope.png"));
    assertTrue(e.getMessage().contains("nope.png"));
  }

  /**
   * Every bundled image and demo asset decodes the same with stb_image as with
   * ImageIO (PNG exactly, JPEG within a small tolerance), so the switch changes no costume.
   */
  @Test
  void everyBundledImageDecodesAsImageIoDecodesIt() throws IOException {
    List<String> problems = new ArrayList<>();
    int checked = 0, skipped = 0;
    for (Path root : new Path[] {Path.of("src/main/resources"), Path.of("../examples/src/main/java")}) {
      if (!Files.isDirectory(root)) {
        continue;
      }
      List<Path> files;
      try (Stream<Path> walk = Files.walk(root)) {
        files = walk.filter(f -> f.toString().matches("(?i).*\\.(png|jpe?g|gif|bmp)")).sorted().toList();
      }
      for (Path f : files) {
        BufferedImage reference = ImageIO.read(f.toFile());
        if (reference == null) {
          skipped++;
          continue;
        }
        Image mine = Image.decode(Files.readAllBytes(f), f.toString());
        checked++;
        if (mine.getWidth() != reference.getWidth() || mine.getHeight() != reference.getHeight()) {
          problems.add(f + ": size " + mine.getWidth() + "x" + mine.getHeight());
          continue;
        }
        boolean jpeg = f.toString().toLowerCase().matches(".*\\.jpe?g");
        int[] theirs = reference.getRGB(0, 0, reference.getWidth(), reference.getHeight(), null, 0,
            reference.getWidth());
        int worst = 0, at = -1;
        for (int i = 0; i < theirs.length; i++) {
          int a = mine.getArgb()[i], b = theirs[i];
          if ((a >>> 24) == 0 && (b >>> 24) == 0) {
            continue; // invisible: the colour does not matter
          }
          int diff = 0;
          for (int shift = 0; shift < 32; shift += 8) {
            diff = Math.max(diff, Math.abs(((a >>> shift) & 0xff) - ((b >>> shift) & 0xff)));
          }
          if (diff > worst) {
            worst = diff;
            at = i;
          }
        }
        if (worst > (jpeg ? 3 : 0)) {
          problems.add(String.format("%s: pixel %d differs by %d (stb %08x, ImageIO %08x)", f, at, worst,
              mine.getArgb()[at], theirs[at]));
        }
      }
    }
    System.out.printf("image parity: %d files compared, %d ImageIO could not read%n", checked, skipped);
    assertTrue(checked > 100, "found only " + checked + " images");
    assertTrue(problems.isEmpty(), String.join("\n", problems));
  }
}
