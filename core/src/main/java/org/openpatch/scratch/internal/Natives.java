package org.openpatch.scratch.internal;

/** Settings LWJGL must see before it loads; call {@link #init()} before the first LWJGL call. */
public final class Natives {

  private static boolean done;

  private Natives() {}

  /**
   * On Java 25 and newer, LWJGL's default memory backend uses {@code sun.misc.Unsafe}, which the
   * JDK warns about; its FFM backend (in the jar's {@code META-INF/versions/25}, hence the fat
   * jar's {@code Multi-Release}) does not. A choice made with the system property wins.
   */
  public static synchronized void init() {
    if (done) {
      return;
    }
    done = true;
    if (Runtime.version().feature() >= 25 && System.getProperty("org.lwjgl.system.memoryBackend") == null) {
      System.setProperty("org.lwjgl.system.memoryBackend", "org.lwjgl.system.MemoryBackendFFM");
    }
  }
}
