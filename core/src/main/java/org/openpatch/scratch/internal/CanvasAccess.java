package org.openpatch.scratch.internal;

import java.util.List;
import org.openpatch.scratch.extension.Canvas;
import org.openpatch.scratch.internal.draw.Item;

/** The core's way to make a {@link Canvas} and take what was recorded, kept out of its public API. */
public final class CanvasAccess {

  /** Implemented by Canvas itself, registered when its class loads. */
  public interface Access {
    Canvas create();

    List<Item> finish(Canvas canvas);
  }

  private static volatile Access access;

  private CanvasAccess() {}

  public static void register(Access implementation) {
    access = implementation;
  }

  private static Access access() {
    if (access == null) {
      try {
        Class.forName(Canvas.class.getName(), true, Canvas.class.getClassLoader());
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }
    }
    return access;
  }

  public static Canvas create() {
    return access().create();
  }

  public static List<Item> finish(Canvas canvas) {
    return access().finish(canvas);
  }
}
