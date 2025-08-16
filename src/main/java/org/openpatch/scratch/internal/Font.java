package org.openpatch.scratch.internal;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import org.openpatch.scratch.extensions.text.Text;
import processing.core.PFont;

/**
 * The Font class represents a font that can be used to render text on the
 * screen. It supports
 * various functionalities such as loading fonts from files, setting font sizes,
 * and getting the
 * font object for a specific size.
 */
public class Font {
  private String name;
  private AbstractMap<Integer, PFont> fontMap;

  private static final AbstractMap<String, AbstractMap<Integer, PFont>> fonts = new ConcurrentHashMap<>();

  /** The default font. */
  public static PFont defaultFont;

  /**
   * Constructs a font with the specified name and path.
   *
   * @param name the name of the font
   * @param path the path to the font file
   */
  public Font(String name, String path) {
    this.name = name;
    this.fontMap = new ConcurrentHashMap<>();

    this.fontMap = loadFont(path);
  }

  /**
   * Copy constructor.
   *
   * @param font the font to copy
   */
  public Font(Font font) {
    this.name = font.name;
    this.fontMap = new ConcurrentHashMap<>(font.fontMap);
  }

  /**
   * Returns the name of the font.
   *
   * @return the name of the font
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the default font.
   *
   * @return the default font
   */
  public static PFont getDefaultFont() {
    if (defaultFont == null) {
      defaultFont = Applet.getInstance()
          .createFont(Text.DEFAULT_FONT, Text.DEFAULT_FONT_SIZE, Text.SMOOTHING);
    }
    return defaultFont;
  }

  /**
   * Loads a font from the specified path and returns a map of font sizes.
   *
   * @param path the path to the font file
   * @return a map of font sizes
   */
  public static AbstractMap<Integer, PFont> loadFont(String path) {
    var fontMap = fonts.getOrDefault(path, new ConcurrentHashMap<>());
    for (int size : Text.FONT_SIZES) {
      if (fontMap.containsKey(size)) {
        return fontMap;
      } else {
        try {
          PFont font = Applet.getInstance().createFont(path, size, Text.SMOOTHING);
          fontMap.put(size, font);
        } catch (Exception e) {
          System.err.println("Error loading font at size " + size + " from path: " + path);
          System.exit(1);
        }
      }
    }
    fonts.put(path, fontMap);
    return fontMap;
  }

  public PFont getFont(int targetSize) {
    int actualSize = Text.FONT_SIZES[0];
    int bestSizeDifference = Math.abs(actualSize - targetSize);

    for (int size : Text.FONT_SIZES) {
      int sizeDifference = Math.abs(size - targetSize);
      if (bestSizeDifference > sizeDifference) {
        bestSizeDifference = sizeDifference;
        actualSize = size;
      }
    }

    return fontMap.get(actualSize);
  }
}
