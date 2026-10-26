package org.openpatch.scratch.internal.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Loads images by path and keeps them: a costume used by many sprites is decoded once. */
public final class Images {

  private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

  private Images() {}

  /**
   * The image at {@code path}: a file relative to the working directory, else a resource on the
   * classpath (the library's built-in images, or a project's assets inside a jar).
   *
   * @throws IllegalArgumentException when there is no such image or it cannot be decoded
   */
  public static Image load(String path) {
    Image cached = CACHE.get(path);
    if (cached != null) {
      return cached;
    }
    // not computeIfAbsent: decoding may take a while and must not block other paths
    Image image = decode(path);
    Image raced = CACHE.putIfAbsent(path, image);
    if (raced != null) {
      return raced;
    }
    org.openpatch.scratch.internal.Assets.noteLoadedLate("image", path);
    return image;
  }

  /** A bundled sprite, turned to face right as sprites do at direction 90 (the atlas says how it is drawn). */
  private static Image builtin(String name) {
    var entry = org.openpatch.scratch.internal.BuiltinAssets.get(name);
    if (entry == null) {
      var close = org.openpatch.scratch.internal.BuiltinAssets.suggest(name, 3);
      throw new IllegalArgumentException("There is no built-in sprite called \"" + name + "\""
          + (close.isEmpty() ? "." : ". Did you mean " + String.join(", ", close) + "?")
          + " An image file needs its extension, e.g. \"" + name + ".png\".");
    }
    Image sheet = load(entry.sheetPath());
    Image picture = sheet.region(entry.x(), entry.y(), entry.width(), entry.height());
    return turnToFaceRight(picture, entry.direction());
  }

  /** Turns a picture drawn facing up (0), down (180) or left (270) so that it faces right. */
  static Image turnToFaceRight(Image image, double direction) {
    double degrees = ((direction % 360) + 360) % 360;
    if (degrees == 90) {
      return image;
    }
    if (degrees % 90 != 0) {
      throw new IllegalArgumentException("A built-in sprite can only face up, right, down or left, not " + direction);
    }
    int w = image.getWidth(), h = image.getHeight();
    boolean quarter = degrees != 270;
    int[] in = image.getArgb(), out = new int[in.length];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int pixel = in[y * w + x];
        if (degrees == 0) {
          out[x * h + (h - 1 - y)] = pixel; // up: the top edge becomes the right edge
        } else if (degrees == 180) {
          out[(w - 1 - x) * h + y] = pixel; // down: the bottom edge becomes the right edge
        } else {
          out[y * w + (w - 1 - x)] = pixel; // left: mirrored
        }
      }
    }
    return quarter ? new Image(h, w, out) : new Image(w, h, out);
  }

  /** The bytes at {@code path} (file first, then classpath), or null. */
  public static byte[] read(String path) {
    try {
      Path file = Path.of(path);
      if (Files.isRegularFile(file)) {
        return Files.readAllBytes(file);
      }
      String resource = path.startsWith("/") ? path.substring(1) : path;
      // the library's own files first: on the module path its folders (images/, sounds/) count
      // as packages of the module, which only a class of the module can read; then the program's
      InputStream in = Images.class.getResourceAsStream("/" + resource);
      if (in == null) {
        in = Images.class.getClassLoader().getResourceAsStream(resource);
      }
      if (in == null && Thread.currentThread().getContextClassLoader() != null) {
        in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
      }
      if (in == null) {
        return null;
      }
      try (InputStream stream = in) {
        return stream.readAllBytes();
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Could not read " + path, e);
    } catch (java.nio.file.InvalidPathException e) {
      return null;
    }
  }

  private static Image decode(String path) {
    if (org.openpatch.scratch.internal.BuiltinAssets.isBuiltinName(path)) {
      return builtin(path);
    }
    byte[] bytes = read(path);
    if (bytes == null) {
      throw new IllegalArgumentException("Image not found: " + path
          + " (looked for a file relative to " + Path.of("").toAbsolutePath() + " and on the classpath)");
    }
    try {
      var format = org.openpatch.scratch.internal.ExtensionHost.imageFormat(path);
      if (format != null) {
        var decoded = format.decode(bytes);
        return new Image(decoded.width(), decoded.height(), decoded.argb());
      }
      return Image.decode(bytes, path);
    } catch (IOException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }
}
