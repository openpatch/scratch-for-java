package org.openpatch.scratch.internal.platform;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;

/**
 * What the core needs from the machine it runs on: a window with an OpenGL 3.3 (or WebGL2)
 * context, sound, image and Ogg decoding, and a few things only a desktop JVM has (class-path
 * folders, exiting). The desktop's is in {@code scratch-desktop} (LWJGL), the browser's in
 * {@code scratch-web} (TeaVM); either registers itself through {@link ServiceLoader}.
 */
public interface Platform {

  /** The OpenGL calls of the renderer, used through {@link GL}. Window thread only. */
  Gl gl();

  /** The window and its input. */
  Display display();

  /** A sound output, or null when the machine has none (sounds stay silent then). */
  AudioDevice openAudio();

  /**
   * Straight (not premultiplied) ARGB pixels of a PNG, JPEG or GIF (its first frame); the desktop
   * also reads BMP and TGA.
   */
  Pixmap decodeImage(byte[] encoded, String name) throws IOException;

  /** Writes a PNG, creating its folder; a platform that cannot write files throws. */
  void writePng(Path file, Pixmap image) throws IOException;

  /** An Ogg Vorbis file as 16-bit samples. */
  Pcm decodeVorbis(byte[] encoded, String name) throws IOException;

  /** A released game's error box: there is no console to read the error in. */
  void showError(String title, String message);

  /** Ends the program. */
  void exit(int status);

  /** Ends the program at once, without shutdown hooks (Studio's preview when it gives up). */
  void halt(int status);

  /**
   * A folder that is on the class path rather than on disk (in a jar or a classes directory), or
   * null. Assets look there after the working directory.
   */
  Path classpathFolder(String folder);

  /**
   * The files under a folder of the library's own resources (e.g. {@code sounds}), as paths
   * relative to it; empty when it cannot be listed.
   */
  List<String> listResources(String folder);

  /** Where a class was loaded from (its jar or folder), or null where that is not known. */
  Object codeLocation(Class<?> type);

  /** Images as the core holds them: straight ARGB, row by row, top first. */
  record Pixmap(int width, int height, int[] argb) {}

  /** Interleaved signed 16-bit samples, little-endian, two bytes each. */
  record Pcm(int channels, int sampleRate, byte[] samples) {}

  /** The platform: the one installed, else the first a {@link ServiceLoader} finds. */
  static Platform get() {
    Platform platform = Holder.platform;
    if (platform == null) {
      synchronized (Holder.class) {
        if (Holder.platform == null) {
          Holder.platform = Holder.find();
        }
        platform = Holder.platform;
      }
    }
    return platform;
  }

  /** Uses {@code platform} from now on; tests, and launchers that know which one they want. */
  static void install(Platform platform) {
    synchronized (Holder.class) {
      Holder.platform = platform;
    }
  }

  /** The found platform; not part of the interface. */
  final class Holder {
    private static volatile Platform platform;

    private Holder() {}

    private static Platform find() {
      for (Platform p : ServiceLoader.load(Platform.class, Platform.class.getClassLoader())) {
        return p;
      }
      ClassLoader context = Thread.currentThread().getContextClassLoader();
      if (context != null) {
        for (Platform p : ServiceLoader.load(Platform.class, context)) {
          return p;
        }
      }
      throw new IllegalStateException("Scratch for Java cannot find its window and sound code. Use the "
          + "scratch-<version>-all.jar, or put scratch-desktop next to scratch on the class path.");
    }
  }
}
