package org.openpatch.scratch.internal;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import org.openpatch.scratch.Text;
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
  private String path;
  private AbstractMap<Integer, PFont> fontMap;

  private static final AbstractMap<String, AbstractMap<Integer, PFont>> fonts = new ConcurrentHashMap<>();

  /** The default font. */
  public static PFont defaultFont;

  /**
   * Constructs a font with the specified name and path. The underlying font
   * file is not actually loaded until {@link #getFont(int)} is first called,
   * so constructing a Font (e.g. via a Sprite that has not been shown yet)
   * does not require a running Applet.
   *
   * @param name the name of the font
   * @param path the path to the font file
   */
  public Font(String name, String path) {
    this.name = name;
    this.path = path;
  }

  /**
   * Copy constructor.
   *
   * @param font the font to copy
   */
  public Font(Font font) {
    this.name = font.name;
    this.path = font.path;
    this.fontMap = font.fontMap == null ? null : new ConcurrentHashMap<>(font.fontMap);
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
  /**
   * Forgets every font loaded so far, so that a changed font setting takes
   * effect instead of the old one lingering in the cache.
   *
   * @ignore-in-docs
   */
  public static void reset() {
    fonts.clear();
    defaultFont = null;
  }

  public static PFont getDefaultFont() {
    if (defaultFont == null) {
      defaultFont = Applet.getInstance()
          .createFont(Text.getDefaultFont(), Text.getDefaultFontSize(), Text.isSmoothing());
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
    for (int size : Text.getDefaultFontSizes()) {
      if (fontMap.containsKey(size)) {
        return fontMap;
      } else {
        try {
          PFont font = Applet.getInstance().createFont(path, size, Text.isSmoothing());
          fontMap.put(size, font);
        } catch (Exception e) {
          AssetErrorReporter.reportAndFail(
              "font", path, "TTF, OTF",
              new String[]{".ttf", ".otf"});
        }
      }
    }
    fonts.put(path, fontMap);
    return fontMap;
  }

  public PFont getFont(int targetSize) {
    if (this.fontMap == null) {
      this.fontMap = loadFont(this.path);
    }

    int actualSize = Text.getDefaultFontSizes()[0];
    int bestSizeDifference = Math.abs(actualSize - targetSize);

    for (int size : Text.getDefaultFontSizes()) {
      int sizeDifference = Math.abs(size - targetSize);
      if (bestSizeDifference > sizeDifference) {
        bestSizeDifference = sizeDifference;
        actualSize = size;
      }
    }

    return fontMap.get(actualSize);
  }
}
