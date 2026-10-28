package org.openpatch.scratch.desktop;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryUtil;
import org.openpatch.scratch.internal.platform.Gl;

/** The renderer's GL on LWJGL's OpenGL 3.3 core bindings; arrays go up through native buffers. */
final class LwjglGl implements Gl {

  /** The vertex upload buffer, grown as batches grow. */
  private FloatBuffer vertices = MemoryUtil.memAllocFloat(14 * 6 * 1024);

  @Override
  public int glGenTextures() {
    return GL33C.glGenTextures();
  }

  @Override
  public void glDeleteTextures(int texture) {
    GL33C.glDeleteTextures(texture);
  }

  @Override
  public void glBindTexture(int target, int texture) {
    GL33C.glBindTexture(target, texture);
  }

  @Override
  public void glActiveTexture(int unit) {
    GL33C.glActiveTexture(unit);
  }

  @Override
  public void glPixelStorei(int pname, int param) {
    GL33C.glPixelStorei(pname, param);
  }

  @Override
  public void glTexParameteri(int target, int pname, int param) {
    GL33C.glTexParameteri(target, pname, param);
  }

  @Override
  public void glGenerateMipmap(int target) {
    GL33C.glGenerateMipmap(target);
  }

  @Override
  public void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, int[] pixels) {
    if (pixels == null) {
      GL33C.glTexImage2D(target, level, internalFormat, width, height, border, format, type, (IntBuffer) null);
    } else {
      GL33C.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }
  }

  @Override
  public void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, byte[] pixels) {
    if (pixels == null) {
      GL33C.glTexImage2D(target, level, internalFormat, width, height, border, format, type, (ByteBuffer) null);
      return;
    }
    ByteBuffer direct = MemoryUtil.memAlloc(Math.max(1, pixels.length)).put(pixels).flip();
    try {
      GL33C.glTexImage2D(target, level, internalFormat, width, height, border, format, type, direct);
    } finally {
      MemoryUtil.memFree(direct);
    }
  }

  @Override
  public void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, int[] pixels) {
    GL33C.glTexSubImage2D(target, level, x, y, width, height, format, type, pixels);
  }

  @Override
  public void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, byte[] pixels) {
    ByteBuffer direct = MemoryUtil.memAlloc(Math.max(1, pixels.length)).put(pixels).flip();
    try {
      GL33C.glTexSubImage2D(target, level, x, y, width, height, format, type, direct);
    } finally {
      MemoryUtil.memFree(direct);
    }
  }

  @Override
  public int glGenFramebuffers() {
    return GL33C.glGenFramebuffers();
  }

  @Override
  public void glDeleteFramebuffers(int framebuffer) {
    GL33C.glDeleteFramebuffers(framebuffer);
  }

  @Override
  public void glBindFramebuffer(int target, int framebuffer) {
    GL33C.glBindFramebuffer(target, framebuffer);
  }

  @Override
  public void glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
    GL33C.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
  }

  @Override
  public int glCheckFramebufferStatus(int target) {
    return GL33C.glCheckFramebufferStatus(target);
  }

  @Override
  public int glGenRenderbuffers() {
    return GL33C.glGenRenderbuffers();
  }

  @Override
  public void glDeleteRenderbuffers(int renderbuffer) {
    GL33C.glDeleteRenderbuffers(renderbuffer);
  }

  @Override
  public void glBindRenderbuffer(int target, int renderbuffer) {
    GL33C.glBindRenderbuffer(target, renderbuffer);
  }

  @Override
  public void glRenderbufferStorageMultisample(int target, int samples, int internalFormat, int width, int height) {
    GL33C.glRenderbufferStorageMultisample(target, samples, internalFormat, width, height);
  }

  @Override
  public void glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer) {
    GL33C.glFramebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer);
  }

  @Override
  public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
    GL33C.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
  }

  @Override
  public void glViewport(int x, int y, int width, int height) {
    GL33C.glViewport(x, y, width, height);
  }

  @Override
  public void glScissor(int x, int y, int width, int height) {
    GL33C.glScissor(x, y, width, height);
  }

  @Override
  public void glReadPixels(int x, int y, int width, int height, int format, int type, int[] pixels) {
    GL33C.glReadPixels(x, y, width, height, format, type, pixels);
  }

  @Override
  public int glGenVertexArrays() {
    return GL33C.glGenVertexArrays();
  }

  @Override
  public void glBindVertexArray(int array) {
    GL33C.glBindVertexArray(array);
  }

  @Override
  public int glGenBuffers() {
    return GL33C.glGenBuffers();
  }

  @Override
  public void glBindBuffer(int target, int buffer) {
    GL33C.glBindBuffer(target, buffer);
  }

  @Override
  public void glBufferData(int target, float[] data, int count, int usage) {
    if (this.vertices.capacity() < count) {
      MemoryUtil.memFree(this.vertices);
      this.vertices = MemoryUtil.memAllocFloat(Math.max(count, this.vertices.capacity() * 2));
    }
    this.vertices.clear();
    this.vertices.put(data, 0, count).flip();
    GL33C.glBufferData(target, this.vertices, usage);
  }

  @Override
  public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
    GL33C.glVertexAttribPointer(index, size, type, normalized, stride, offset);
  }

  @Override
  public void glEnableVertexAttribArray(int index) {
    GL33C.glEnableVertexAttribArray(index);
  }

  @Override
  public void glDrawArrays(int mode, int first, int count) {
    GL33C.glDrawArrays(mode, first, count);
  }

  @Override
  public void glClearColor(float red, float green, float blue, float alpha) {
    GL33C.glClearColor(red, green, blue, alpha);
  }

  @Override
  public void glClear(int mask) {
    GL33C.glClear(mask);
  }

  @Override
  public void glEnable(int capability) {
    GL33C.glEnable(capability);
  }

  @Override
  public void glDisable(int capability) {
    GL33C.glDisable(capability);
  }

  @Override
  public void glBlendFunc(int source, int destination) {
    GL33C.glBlendFunc(source, destination);
  }

  @Override
  public int glGetInteger(int pname) {
    return GL33C.glGetInteger(pname);
  }

  @Override
  public void glGetIntegerv(int pname, int[] params) {
    GL33C.glGetIntegerv(pname, params);
  }

  @Override
  public String glGetString(int name) {
    return GL33C.glGetString(name);
  }

  @Override
  public int glCreateShader(int type) {
    return GL33C.glCreateShader(type);
  }

  @Override
  public void glShaderSource(int shader, CharSequence source) {
    GL33C.glShaderSource(shader, source);
  }

  @Override
  public void glCompileShader(int shader) {
    GL33C.glCompileShader(shader);
  }

  @Override
  public int glGetShaderi(int shader, int pname) {
    return GL33C.glGetShaderi(shader, pname);
  }

  @Override
  public String glGetShaderInfoLog(int shader) {
    return GL33C.glGetShaderInfoLog(shader);
  }

  @Override
  public void glDeleteShader(int shader) {
    GL33C.glDeleteShader(shader);
  }

  @Override
  public int glCreateProgram() {
    return GL33C.glCreateProgram();
  }

  @Override
  public void glAttachShader(int program, int shader) {
    GL33C.glAttachShader(program, shader);
  }

  @Override
  public void glLinkProgram(int program) {
    GL33C.glLinkProgram(program);
  }

  @Override
  public int glGetProgrami(int program, int pname) {
    return GL33C.glGetProgrami(program, pname);
  }

  @Override
  public String glGetProgramInfoLog(int program) {
    return GL33C.glGetProgramInfoLog(program);
  }

  @Override
  public void glDeleteProgram(int program) {
    GL33C.glDeleteProgram(program);
  }

  @Override
  public void glUseProgram(int program) {
    GL33C.glUseProgram(program);
  }

  @Override
  public int glGetUniformLocation(int program, CharSequence name) {
    return GL33C.glGetUniformLocation(program, name);
  }

  @Override
  public void glUniform1f(int location, float v0) {
    GL33C.glUniform1f(location, v0);
  }

  @Override
  public void glUniform2f(int location, float v0, float v1) {
    GL33C.glUniform2f(location, v0, v1);
  }

  @Override
  public void glUniform3f(int location, float v0, float v1, float v2) {
    GL33C.glUniform3f(location, v0, v1, v2);
  }

  @Override
  public void glUniform4f(int location, float v0, float v1, float v2, float v3) {
    GL33C.glUniform4f(location, v0, v1, v2, v3);
  }

  @Override
  public void glUniform1i(int location, int v0) {
    GL33C.glUniform1i(location, v0);
  }

  @Override
  public void glUniform1fv(int location, float[] values) {
    GL33C.glUniform1fv(location, values);
  }

  @Override
  public void glUniform2fv(int location, float[] values) {
    GL33C.glUniform2fv(location, values);
  }

  @Override
  public void glUniform3fv(int location, float[] values) {
    GL33C.glUniform3fv(location, values);
  }

  @Override
  public void glUniform4fv(int location, float[] values) {
    GL33C.glUniform4fv(location, values);
  }

  @Override
  public void glUniformMatrix3fv(int location, boolean transpose, float[] values) {
    GL33C.glUniformMatrix3fv(location, transpose, values);
  }
}
