package org.openpatch.scratch.internal;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.openpatch.scratch.internal.image.Image;

/** The rectangle around an image's painted pixels, cached per image (images never change). */
final class ContentBounds {

  private static final Map<Image, double[]> CACHE = Collections.synchronizedMap(new WeakHashMap<>());

  private ContentBounds() {}

  /** A ring {left, top, right, top, right, bottom, left, bottom}; callers must not change it. */
  static double[] of(Image image) {
    return CACHE.computeIfAbsent(image, ContentBounds::compute);
  }

  private static double[] compute(Image image) {
    int w = image.getWidth(), h = image.getHeight();
    int[] argb = image.getArgb();
    int left = w, right = -1, top = h, bottom = -1;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if ((argb[y * w + x] >>> 24) != 0) {
          left = Math.min(left, x);
          right = Math.max(right, x);
          top = Math.min(top, y);
          bottom = Math.max(bottom, y);
        }
      }
    }
    if (right < 0) {
      left = 0;
      top = 0;
      right = w - 1;
      bottom = h - 1;
    }
    double l = left, t = top, r = right + 1, b = bottom + 1;
    return new double[] {l, t, r, t, r, b, l, b};
  }
}
