package docs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.openpatch.scratch.internal.BuiltinAssets;

/**
 * Reads the bundled sprite sheets so the documentation can preview the painted
 * part of a sprite rather than its whole cell.
 */
final class Sheets {

  /** A pixel counts as painted above this alpha value. */
  private static final int ALPHA_THRESHOLD = 8;

  private static final Map<String, BufferedImage> sheets = new HashMap<>();

  private Sheets() {
  }

  /**
   * Returns the painted area within a sprite's cell.
   *
   * @param entry the sprite to measure
   * @return x and y offset inside the cell, then width and height; the whole
   *         cell if nothing is painted or the sheet cannot be read
   */
  static int[] contentBox(BuiltinAssets.Entry entry) {
    int[] wholeCell = { 0, 0, entry.width, entry.height };

    BufferedImage sheet = load(entry.sheetPath);
    if (sheet == null) {
      return wholeCell;
    }

    int[] pixels = sheet.getRGB(entry.x, entry.y, entry.width, entry.height,
        null, 0, entry.width);

    int minX = entry.width;
    int minY = entry.height;
    int maxX = -1;
    int maxY = -1;

    for (int y = 0; y < entry.height; y++) {
      for (int x = 0; x < entry.width; x++) {
        int alpha = (pixels[y * entry.width + x] >>> 24);
        if (alpha > ALPHA_THRESHOLD) {
          if (x < minX) {
            minX = x;
          }
          if (x > maxX) {
            maxX = x;
          }
          if (y < minY) {
            minY = y;
          }
          if (y > maxY) {
            maxY = y;
          }
        }
      }
    }

    if (maxX < 0) {
      return wholeCell;
    }
    return new int[] { minX, minY, maxX - minX + 1, maxY - minY + 1 };
  }

  private static BufferedImage load(String resourcePath) {
    if (sheets.containsKey(resourcePath)) {
      return sheets.get(resourcePath);
    }

    BufferedImage image = null;
    try (InputStream in = Sheets.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (in != null) {
        image = ImageIO.read(in);
      }
    } catch (IOException e) {
      // Falling back to the whole cell is good enough for a preview.
    }

    sheets.put(resourcePath, image);
    return image;
  }
}
