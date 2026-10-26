package spike;

import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointBitmapBoxSubpixel;
import static org.lwjgl.stb.STBTruetype.stbtt_MakeCodepointBitmapSubpixel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import org.lwjgl.system.MemoryUtil;

/**
 * P1-06: Jersey 10 and UbuntuMono through stb_truetype, laid out at the nominal
 * size and drawn at several scales. No window, no OpenGL - the CPU rasterises
 * and stb_image_write saves PNGs.
 */
public class TextCheck {

  private static final double MAX_WIDTH = 220;
  private static final String TEXT =
      "Jörg fährt größere Straßen – „Hallo“ … 12,50 € 90° ÄÖÜ ß. "
          + "Supercalifragilisticexpialidocious-wordthatistoolong";

  public static void main(String[] args) throws IOException {
    // LWJGL's default backend uses sun.misc.Unsafe, which Java 24+ warns about
    // in red. Its FFM backend does not. Has to be set before the first LWJGL
    // class that touches memory loads.
    if (Runtime.version().feature() >= 25 && System.getProperty("org.lwjgl.system.memoryBackend") == null) {
      System.setProperty("org.lwjgl.system.memoryBackend", "org.lwjgl.system.MemoryBackendFFM");
    }
    Path dir = Path.of("target/text");
    Files.createDirectories(dir);
    try (FontFile mono = new FontFile("../../core/src/main/resources/UbuntuMono-Regular.ttf", "UbuntuMono");
        FontFile jersey = new FontFile("../../core/src/main/resources/Jersey10-Regular.ttf", "Jersey10")) {
      print(mono);
      print(jersey);
      check(mono, mono, 14, new double[] {1, 1.5, 2}, false, dir);
      check(jersey, mono, 18.6667, new double[] {1, 2, 3, 4}, true, dir);
    }
    System.out.println("text: done");
  }

  private static void print(FontFile font) {
    System.out.printf(Locale.ROOT,
        "text: %s: unitsPerEm %d, ascent %d, descent %d, lineGap %d, %d glyphs, pixel grid %d%n",
        font.name, font.unitsPerEm, font.ascent, font.descent, font.lineGap, font.numGlyphs, font.pixelGrid);
  }

  private static void check(FontFile font, FontFile fallback, double size, double[] scales,
      boolean pixelArt, Path dir) {
    long start = System.nanoTime();
    TextLayout reference = TextLayout.layout(font, fallback, TEXT, size, MAX_WIDTH);
    double layoutMs = (System.nanoTime() - start) / 1e6;
    System.out.printf(Locale.ROOT, "text: %s at %s: %d lines, box %.2f x %.2f, layout %.2f ms%n",
        font.name, tag(size), reference.lines.size(), reference.boxWidth, reference.boxHeight, layoutMs);
    for (TextLayout.Line line : reference.lines) {
      System.out.printf(Locale.ROOT, "text:   |%s| %.2f%n", line.text(), line.width());
    }
    for (double scale : scales) {
      TextLayout again = TextLayout.layout(font, fallback, TEXT, size, MAX_WIDTH);
      if (again.lines.size() != reference.lines.size() || again.boxWidth != reference.boxWidth
          || again.boxHeight != reference.boxHeight) {
        throw new IllegalStateException(font.name + ": lines or box differ at " + tag(scale) + "x");
      }
      for (int i = 0; i < again.lines.size(); i++) {
        if (!again.lines.get(i).text().equals(reference.lines.get(i).text())) {
          throw new IllegalStateException(font.name + ": line " + i + " differs at " + tag(scale) + "x");
        }
      }
      start = System.nanoTime();
      int[] argb = render(again, scale, pixelArt);
      double rasterMs = (System.nanoTime() - start) / 1e6;
      int width = (int) Math.ceil(again.boxWidth * scale);
      int height = (int) Math.ceil(again.boxHeight * scale);
      writePng(dir, font, size, scale, argb, width, height);
      System.out.printf(Locale.ROOT, "text: %s at %sx: %d x %d px, raster %.2f ms%n",
          font.name, tag(scale), width, height, rasterMs);
    }
  }

  /** ARGB pixels, white background, black text, device size = box x scale. */
  private static int[] render(TextLayout layout, double scale, boolean pixelArt) {
    int width = (int) Math.ceil(layout.boxWidth * scale);
    int height = (int) Math.ceil(layout.boxHeight * scale);
    int[] argb = new int[width * height];
    Arrays.fill(argb, 0xFFFFFFFF);
    for (TextLayout.Line line : layout.lines) {
      for (TextLayout.Glyph glyph : line.glyphs()) {
        FontFile font = glyph.font();
        float raster = font.emScale(layout.size) * (float) scale;
        double x = glyph.x() * scale;
        double y = glyph.baselineY() * scale;
        // pixel art snaps origins to whole device pixels and takes no sub-pixel shift
        int originX = pixelArt ? Math.round((float) x) : (int) Math.floor(x);
        int originY = pixelArt ? Math.round((float) y) : (int) Math.floor(y);
        float shiftX = pixelArt ? 0 : (float) (x - originX);
        float shiftY = pixelArt ? 0 : (float) (y - originY);
        int[] x0 = new int[1], y0 = new int[1], x1 = new int[1], y1 = new int[1];
        stbtt_GetCodepointBitmapBoxSubpixel(font.info, glyph.codepoint(), raster, raster,
            shiftX, shiftY, x0, y0, x1, y1);
        int bitmapWidth = x1[0] - x0[0];
        int bitmapHeight = y1[0] - y0[0];
        if (bitmapWidth <= 0 || bitmapHeight <= 0) {
          continue; // a space
        }
        ByteBuffer coverage = MemoryUtil.memAlloc(bitmapWidth * bitmapHeight);
        stbtt_MakeCodepointBitmapSubpixel(font.info, coverage, bitmapWidth, bitmapHeight, bitmapWidth,
            raster, raster, shiftX, shiftY, glyph.codepoint());
        for (int row = 0; row < bitmapHeight; row++) {
          int deviceY = originY + y0[0] + row;
          if (deviceY < 0 || deviceY >= height) {
            continue;
          }
          for (int column = 0; column < bitmapWidth; column++) {
            int deviceX = originX + x0[0] + column;
            if (deviceX < 0 || deviceX >= width) {
              continue;
            }
            int c = coverage.get(row * bitmapWidth + column) & 0xff;
            int pixel = deviceY * width + deviceX;
            if (pixelArt) {
              if (c >= 128) { // thresholded: half covered is on
                argb[pixel] = 0xFF000000;
              }
            } else {
              // black coverage over what is there, white or a previous glyph
              int gray = (int) ((argb[pixel] >>> 16 & 0xff) * (255 - c) / 255);
              argb[pixel] = 0xFF000000 | gray << 16 | gray << 8 | gray;
            }
          }
        }
        MemoryUtil.memFree(coverage);
      }
    }
    return argb;
  }

  private static void writePng(Path dir, FontFile font, double size, double scale,
      int[] argb, int width, int height) {
    ByteBuffer rgba = MemoryUtil.memAlloc(argb.length * 4);
    for (int pixel : argb) {
      rgba.put((byte) (pixel >> 16)).put((byte) (pixel >> 8)).put((byte) pixel)
          .put((byte) (pixel >>> 24));
    }
    rgba.flip();
    Path file = dir.resolve(String.format(Locale.ROOT, "%s-%s-%sx.png", font.name, tag(size), tag(scale)));
    if (!stbi_write_png(file.toString(), width, height, 4, rgba, width * 4)) {
      throw new IllegalStateException("could not write " + file);
    }
    MemoryUtil.memFree(rgba);
  }

  /** 14, 1.5 or 18.6667 - a whole number without a point, %g without trailing zeros. */
  private static String tag(double value) {
    return value == Math.rint(value) ? String.valueOf((long) value)
        : String.format(Locale.ROOT, "%.6g", value);
  }
}
