package org.openpatch.scratch.internal.platform;

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

/**
 * The core's tests run headless: no window, no GL, no sound device. Images and Ogg files decode
 * with stb, as on the desktop (scratch-desktop cannot be a test dependency: it depends on the
 * core), so colour sensing and hitboxes see the same pixels as there.
 */
public final class TestPlatform implements Platform {

  @Override
  public Gl gl() {
    throw new UnsupportedOperationException("headless: no GL in the core's tests");
  }

  @Override
  public Display display() {
    throw new UnsupportedOperationException("headless: no window in the core's tests");
  }

  @Override
  public AudioDevice openAudio() {
    return null;
  }

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
        int[] argb = new int[w.get(0) * h.get(0)];
        for (int i = 0; i < argb.length; i++) {
          argb[i] = (rgba.get(4 * i + 3) & 0xff) << 24 | (rgba.get(4 * i) & 0xff) << 16
              | (rgba.get(4 * i + 1) & 0xff) << 8 | rgba.get(4 * i + 2) & 0xff;
        }
        return new Pixmap(w.get(0), h.get(0), argb);
      } finally {
        STBImage.stbi_image_free(rgba);
      }
    } finally {
      MemoryUtil.memFree(in);
    }
  }

  @Override
  public void writePng(Path file, Pixmap image) throws IOException {
    ByteBuffer rgba = MemoryUtil.memAlloc(Math.max(1, image.argb().length * 4));
    try {
      for (int p : image.argb()) {
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
    IntBuffer channels = BufferUtils.createIntBuffer(1), rate = BufferUtils.createIntBuffer(1);
    ShortBuffer pcm = STBVorbis.stb_vorbis_decode_memory(in, channels, rate);
    MemoryUtil.memFree(in);
    if (pcm == null) {
      throw new IOException("stb_vorbis could not decode " + name);
    }
    byte[] bytes = new byte[pcm.remaining() * 2];
    for (int i = 0; i < bytes.length; i += 2) {
      short sample = pcm.get();
      bytes[i] = (byte) sample;
      bytes[i + 1] = (byte) (sample >> 8);
    }
    MemoryUtil.memFree(pcm);
    return new Pcm(channels.get(0), rate.get(0), bytes);
  }

  @Override
  public void showError(String title, String message) {
    System.err.println(title + ": " + message);
  }

  @Override
  public void exit(int status) {
    throw new IllegalStateException("the program asked to exit with " + status);
  }

  @Override
  public void halt(int status) {
    throw new IllegalStateException("the program asked to halt with " + status);
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
