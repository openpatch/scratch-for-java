package org.openpatch.scratch.desktop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.openpatch.scratch.internal.platform.AudioDevice;
import org.openpatch.scratch.internal.platform.Display;
import org.openpatch.scratch.internal.platform.Gl;
import org.openpatch.scratch.internal.platform.JvmResources;
import org.openpatch.scratch.internal.platform.Platform;

/**
 * Scratch for Java on a desktop JVM: GLFW, OpenGL 3.3, OpenAL Soft, stb_image, stb_vorbis and
 * tinyfd through LWJGL. Found by the core through {@link java.util.ServiceLoader}.
 */
public final class DesktopPlatform implements Platform {

  private final Gl gl;
  private final Display display;

  public DesktopPlatform() {
    // On Java 25 and newer, LWJGL's default memory backend uses sun.misc.Unsafe, which the JDK warns
    // about; its FFM backend (in the jar's META-INF/versions/25, hence the fat jar's Multi-Release)
    // does not. A choice made with the system property wins. Before LWJGL's first call.
    if (Runtime.version().feature() >= 25 && System.getProperty("org.lwjgl.system.memoryBackend") == null) {
      System.setProperty("org.lwjgl.system.memoryBackend", "org.lwjgl.system.MemoryBackendFFM");
    }
    this.gl = new LwjglGl();
    this.display = new GlfwDisplay();
  }

  @Override
  public Gl gl() {
    return this.gl;
  }

  @Override
  public Display display() {
    return this.display;
  }

  @Override
  public AudioDevice openAudio() {
    return OpenAlDevice.open();
  }

  /** stb_image: no global flags, as loading runs on several threads at once. */
  @Override
  public Pixmap decodeImage(byte[] encoded, String name) throws IOException {
    ByteBuffer in = MemoryUtil.memAlloc(Math.max(1, encoded.length));
    try (MemoryStack stack = MemoryStack.stackPush()) {
      in.put(encoded).flip();
      IntBuffer w = stack.mallocInt(1), h = stack.mallocInt(1), channels = stack.mallocInt(1);
      ByteBuffer rgba = STBImage.stbi_load_from_memory(in, w, h, channels, 4);
      if (rgba == null) {
        throw new IOException("Could not decode " + name + ": " + STBImage.stbi_failure_reason());
      }
      try {
        int width = w.get(0), height = h.get(0);
        int[] argb = new int[width * height];
        for (int i = 0; i < argb.length; i++) {
          int r = rgba.get(4 * i) & 0xff, g = rgba.get(4 * i + 1) & 0xff, b = rgba.get(4 * i + 2) & 0xff;
          int a = rgba.get(4 * i + 3) & 0xff;
          argb[i] = a << 24 | r << 16 | g << 8 | b;
        }
        return new Pixmap(width, height, argb);
      } finally {
        STBImage.stbi_image_free(rgba);
      }
    } finally {
      MemoryUtil.memFree(in);
    }
  }

  @Override
  public void writePng(Path file, Pixmap image) throws IOException {
    int[] argb = image.argb();
    ByteBuffer rgba = MemoryUtil.memAlloc(Math.max(1, argb.length * 4));
    try {
      for (int p : argb) {
        rgba.put((byte) (p >> 16)).put((byte) (p >> 8)).put((byte) p).put((byte) (p >>> 24));
      }
      rgba.flip();
      Path absolute = file.toAbsolutePath();
      if (absolute.getParent() != null) {
        java.nio.file.Files.createDirectories(absolute.getParent());
      }
      if (!STBImageWrite.stbi_write_png(absolute.toString(), image.width(), image.height(), 4, rgba,
          image.width() * 4)) {
        throw new IOException("Could not write " + file);
      }
    } finally {
      MemoryUtil.memFree(rgba);
    }
  }

  @Override
  public Pcm decodeVorbis(byte[] encoded, String name) throws IOException {
    ByteBuffer in = MemoryUtil.memAlloc(encoded.length).put(encoded).flip();
    IntBuffer channels = BufferUtils.createIntBuffer(1);
    IntBuffer rate = BufferUtils.createIntBuffer(1);
    ShortBuffer pcm = STBVorbis.stb_vorbis_decode_memory(in, channels, rate);
    MemoryUtil.memFree(in); // the decode buffer is ours
    if (pcm == null) {
      throw new IOException("stb_vorbis could not decode " + name);
    }
    int count = pcm.remaining();
    byte[] bytes = new byte[count * 2];
    for (int i = 0; i < count; i++) {
      short sample = pcm.get();
      // little-endian, as WAV and the audio device have it: the low byte comes first
      bytes[2 * i] = (byte) sample;
      bytes[2 * i + 1] = (byte) (sample >> 8);
    }
    MemoryUtil.memFree(pcm); // the decode buffer is ours
    return new Pcm(channels.get(0), rate.get(0), bytes);
  }

  @Override
  public void showError(String title, String message) {
    TinyFileDialogs.tinyfd_messageBox(title, message, "ok", "error", 1);
  }

  @Override
  public void exit(int status) {
    System.exit(status);
  }

  @Override
  public void halt(int status) {
    Runtime.getRuntime().halt(status);
  }

  @Override
  public Path classpathFolder(String folder) {
    return JvmResources.classpathFolder(folder);
  }

  @Override
  public List<String> listResources(String folder) {
    return JvmResources.listResources(folder);
  }

  @Override
  public Object codeLocation(Class<?> type) {
    return JvmResources.codeLocation(type);
  }
}
