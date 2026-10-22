package org.openpatch.scratch.internal.render;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;

/** Textures, render targets and a vertex batch: the renderer's GPU objects. Window thread only. */
final class Gpu {

  private Gpu() {}

  /** Straight ARGB ints → premultiplied ARGB ints, as every texture holds them. */
  static int[] premultiply(int[] argb) {
    int[] out = new int[argb.length];
    for (int i = 0; i < argb.length; i++) {
      int c = argb[i], a = c >>> 24;
      if (a == 255) {
        out[i] = c;
      } else if (a != 0) {
        int r = ((c >> 16) & 0xff) * a / 255, g = ((c >> 8) & 0xff) * a / 255, b = (c & 0xff) * a / 255;
        out[i] = a << 24 | r << 16 | g << 8 | b;
      }
    }
    return out;
  }

  /** A texture. ARGB ints go up as GL_BGRA / GL_UNSIGNED_INT_8_8_8_8_REV: no byte shuffling. */
  static final class Texture {
    final int id;
    final int width, height;
    boolean smooth;

    Texture(int width, int height, int[] argbPremultiplied, boolean smooth, boolean mipmaps) {
      this.id = glGenTextures();
      this.width = width;
      this.height = height;
      glBindTexture(GL_TEXTURE_2D, this.id);
      glPixelStorei(GL_UNPACK_ALIGNMENT, 4);
      glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
      if (argbPremultiplied == null) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV,
            (IntBuffer) null);
      } else {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV,
            argbPremultiplied);
      }
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
      this.setSmooth(smooth, mipmaps);
    }

    void setSmooth(boolean smooth, boolean mipmaps) {
      this.smooth = smooth;
      glBindTexture(GL_TEXTURE_2D, this.id);
      if (smooth && mipmaps) {
        glGenerateMipmap(GL_TEXTURE_2D);
      }
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
          smooth ? (mipmaps ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR) : GL_NEAREST);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, smooth ? GL_LINEAR : GL_NEAREST);
    }

    /** Replaces a region; {@code argb} straight, premultiplied here. */
    void update(int x, int y, int w, int h, int[] argb) {
      glBindTexture(GL_TEXTURE_2D, this.id);
      glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
      glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, w, h, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, premultiply(argb));
    }

    void delete() {
      glDeleteTextures(this.id);
    }
  }

  /**
   * Something to draw into: a texture, optionally behind a multisampled buffer that is resolved
   * into it.
   */
  static final class Target {
    final int width, height;
    final Texture texture;
    private final int fbo;
    private final int msaaFbo, msaaBuffer;

    Target(int width, int height, int samples, boolean smooth) {
      this.width = width;
      this.height = height;
      this.texture = new Texture(width, height, null, smooth, false);
      this.fbo = glGenFramebuffers();
      glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
      glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.id, 0);
      check("target");
      if (samples > 1) {
        this.msaaFbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.msaaFbo);
        this.msaaBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, this.msaaBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, this.msaaBuffer);
        check("multisampled target");
      } else {
        this.msaaFbo = 0;
        this.msaaBuffer = 0;
      }
      glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /** Draw into it from now on, the whole of it. */
    void bind() {
      glBindFramebuffer(GL_FRAMEBUFFER, this.msaaFbo != 0 ? this.msaaFbo : this.fbo);
      glViewport(0, 0, this.width, this.height);
    }

    /** Makes what was drawn readable in {@link #texture}. */
    void resolve() {
      if (this.msaaFbo != 0) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.msaaFbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.fbo);
        glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, this.width, this.height, GL_COLOR_BUFFER_BIT,
            GL_NEAREST);
      }
      glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * The resolved pixels as straight ARGB, top row first. Targets are drawn with their top row
     * at row 0 (the renderer flips its projection for them), so no rows are swapped here.
     */
    int[] read() {
      glBindFramebuffer(GL_READ_FRAMEBUFFER, this.fbo);
      int[] out = new int[this.width * this.height];
      glReadPixels(0, 0, this.width, this.height, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, out);
      glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
      for (int i = 0; i < out.length; i++) {
        int c = out[i], a = c >>> 24;
        if (a != 0 && a != 255) {
          int r = Math.min(255, ((c >> 16) & 0xff) * 255 / a), g = Math.min(255, ((c >> 8) & 0xff) * 255 / a);
          int b = Math.min(255, (c & 0xff) * 255 / a);
          out[i] = a << 24 | r << 16 | g << 8 | b;
        }
      }
      return out;
    }

    void delete() {
      this.texture.delete();
      glDeleteFramebuffers(this.fbo);
      if (this.msaaFbo != 0) {
        glDeleteFramebuffers(this.msaaFbo);
        glDeleteRenderbuffers(this.msaaBuffer);
      }
    }

    private static void check(String what) {
      int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
      if (status != GL_FRAMEBUFFER_COMPLETE) {
        throw new IllegalStateException(what + " incomplete: 0x" + Integer.toHexString(status));
      }
    }
  }

  /**
   * Triangles with position (pixels), texture coordinates and a premultiplied colour, sent in one
   * buffer upload per flush.
   */
  static final class Batch {
    static final int FLOATS = 8;
    private final int vao, vbo;
    private FloatBuffer data = MemoryUtil.memAllocFloat(FLOATS * 6 * 1024);
    private int vertices;

    Batch() {
      this.vao = glGenVertexArrays();
      glBindVertexArray(this.vao);
      this.vbo = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
      glVertexAttribPointer(0, 2, GL_FLOAT, false, FLOATS * Float.BYTES, 0);
      glEnableVertexAttribArray(0);
      glVertexAttribPointer(1, 2, GL_FLOAT, false, FLOATS * Float.BYTES, 2L * Float.BYTES);
      glEnableVertexAttribArray(1);
      glVertexAttribPointer(2, 4, GL_FLOAT, false, FLOATS * Float.BYTES, 4L * Float.BYTES);
      glEnableVertexAttribArray(2);
    }

    void vertex(double x, double y, double u, double v, float r, float g, float b, float a) {
      if (this.data.remaining() < FLOATS) {
        FloatBuffer bigger = MemoryUtil.memAllocFloat(this.data.capacity() * 2);
        this.data.flip();
        bigger.put(this.data);
        MemoryUtil.memFree(this.data);
        this.data = bigger;
      }
      this.data.put((float) x).put((float) y).put((float) u).put((float) v).put(r).put(g).put(b).put(a);
      this.vertices++;
    }

    /** A quad as two triangles: corners in order top-left, top-right, bottom-right, bottom-left. */
    void quad(double[] xy, double u0, double v0, double u1, double v1, float r, float g, float b, float a) {
      this.vertex(xy[0], xy[1], u0, v0, r, g, b, a);
      this.vertex(xy[2], xy[3], u1, v0, r, g, b, a);
      this.vertex(xy[4], xy[5], u1, v1, r, g, b, a);
      this.vertex(xy[0], xy[1], u0, v0, r, g, b, a);
      this.vertex(xy[4], xy[5], u1, v1, r, g, b, a);
      this.vertex(xy[6], xy[7], u0, v1, r, g, b, a);
    }

    boolean isEmpty() {
      return this.vertices == 0;
    }

    /** Draws what was collected and starts over. */
    void flush() {
      if (this.vertices == 0) {
        return;
      }
      this.data.flip();
      glBindVertexArray(this.vao);
      glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
      glBufferData(GL_ARRAY_BUFFER, this.data, GL_STREAM_DRAW);
      glDrawArrays(GL_TRIANGLES, 0, this.vertices);
      this.data.clear();
      this.vertices = 0;
    }
  }

  /** Bytes of one channel uploaded as GL_RED, for glyph coverage. */
  static int coverageTexture(int width, int height) {
    int id = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, id);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_R8, width, height, 0, GL_RED, GL_UNSIGNED_BYTE, (ByteBuffer) null);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    return id;
  }
}
