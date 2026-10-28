package org.openpatch.scratch.internal.platform;

/**
 * The renderer's OpenGL, by LWJGL's names and constants, on the platform's {@link Gl}:
 * {@code import static org.openpatch.scratch.internal.platform.GL.*}. Window thread only.
 */
public final class GL {

  private static final Gl IMPL = Platform.get().gl();

  private GL() {}

  public static final int GL_ARRAY_BUFFER = 34962;
  public static final int GL_BGRA = 32993;
  public static final int GL_BLEND = 3042;
  public static final int GL_CLAMP_TO_EDGE = 33071;
  public static final int GL_COLOR_ATTACHMENT0 = 36064;
  public static final int GL_COLOR_BUFFER_BIT = 16384;
  public static final int GL_COMPILE_STATUS = 35713;
  public static final int GL_DRAW_FRAMEBUFFER = 36009;
  public static final int GL_FALSE = 0;
  public static final int GL_FLOAT = 5126;
  public static final int GL_FRAGMENT_SHADER = 35632;
  public static final int GL_FRAMEBUFFER = 36160;
  public static final int GL_FRAMEBUFFER_BINDING = 36006;
  public static final int GL_FRAMEBUFFER_COMPLETE = 36053;
  public static final int GL_LINEAR = 9729;
  public static final int GL_LINEAR_MIPMAP_LINEAR = 9987;
  public static final int GL_LINK_STATUS = 35714;
  public static final int GL_NEAREST = 9728;
  public static final int GL_ONE = 1;
  public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
  public static final int GL_R8 = 33321;
  public static final int GL_READ_FRAMEBUFFER = 36008;
  public static final int GL_RED = 6403;
  public static final int GL_RENDERBUFFER = 36161;
  public static final int GL_RENDERER = 7937;
  public static final int GL_RGBA8 = 32856;
  public static final int GL_SCISSOR_TEST = 3089;
  public static final int GL_STREAM_DRAW = 35040;
  public static final int GL_TEXTURE0 = 33984;
  public static final int GL_TEXTURE1 = 33985;
  public static final int GL_TEXTURE_2D = 3553;
  public static final int GL_TEXTURE_MAG_FILTER = 10240;
  public static final int GL_TEXTURE_MAX_LEVEL = 33085;
  public static final int GL_TEXTURE_MIN_FILTER = 10241;
  public static final int GL_TEXTURE_WRAP_S = 10242;
  public static final int GL_TEXTURE_WRAP_T = 10243;
  public static final int GL_TRIANGLES = 4;
  public static final int GL_UNPACK_ALIGNMENT = 3317;
  public static final int GL_UNPACK_ROW_LENGTH = 3314;
  public static final int GL_UNSIGNED_BYTE = 5121;
  public static final int GL_UNSIGNED_INT_8_8_8_8_REV = 33639;
  public static final int GL_VERSION = 7938;
  public static final int GL_VERTEX_SHADER = 35633;
  public static final int GL_VIEWPORT = 2978;

  public static int glGenTextures() {
    return IMPL.glGenTextures();
  }

  public static void glDeleteTextures(int texture) {
    IMPL.glDeleteTextures(texture);
  }

  public static void glBindTexture(int target, int texture) {
    IMPL.glBindTexture(target, texture);
  }

  public static void glActiveTexture(int unit) {
    IMPL.glActiveTexture(unit);
  }

  public static void glPixelStorei(int pname, int param) {
    IMPL.glPixelStorei(pname, param);
  }

  public static void glTexParameteri(int target, int pname, int param) {
    IMPL.glTexParameteri(target, pname, param);
  }

  public static void glGenerateMipmap(int target) {
    IMPL.glGenerateMipmap(target);
  }

  public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, int[] pixels) {
    IMPL.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
  }

  public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, byte[] pixels) {
    IMPL.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
  }

  public static void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, int[] pixels) {
    IMPL.glTexSubImage2D(target, level, x, y, width, height, format, type, pixels);
  }

  public static void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, byte[] pixels) {
    IMPL.glTexSubImage2D(target, level, x, y, width, height, format, type, pixels);
  }

  public static int glGenFramebuffers() {
    return IMPL.glGenFramebuffers();
  }

  public static void glDeleteFramebuffers(int framebuffer) {
    IMPL.glDeleteFramebuffers(framebuffer);
  }

  public static void glBindFramebuffer(int target, int framebuffer) {
    IMPL.glBindFramebuffer(target, framebuffer);
  }

  public static void glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
    IMPL.glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
  }

  public static int glCheckFramebufferStatus(int target) {
    return IMPL.glCheckFramebufferStatus(target);
  }

  public static int glGenRenderbuffers() {
    return IMPL.glGenRenderbuffers();
  }

  public static void glDeleteRenderbuffers(int renderbuffer) {
    IMPL.glDeleteRenderbuffers(renderbuffer);
  }

  public static void glBindRenderbuffer(int target, int renderbuffer) {
    IMPL.glBindRenderbuffer(target, renderbuffer);
  }

  public static void glRenderbufferStorageMultisample(int target, int samples, int internalFormat, int width, int height) {
    IMPL.glRenderbufferStorageMultisample(target, samples, internalFormat, width, height);
  }

  public static void glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer) {
    IMPL.glFramebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer);
  }

  public static void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
    IMPL.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
  }

  public static void glViewport(int x, int y, int width, int height) {
    IMPL.glViewport(x, y, width, height);
  }

  public static void glScissor(int x, int y, int width, int height) {
    IMPL.glScissor(x, y, width, height);
  }

  public static void glReadPixels(int x, int y, int width, int height, int format, int type, int[] pixels) {
    IMPL.glReadPixels(x, y, width, height, format, type, pixels);
  }

  public static int glGenVertexArrays() {
    return IMPL.glGenVertexArrays();
  }

  public static void glBindVertexArray(int array) {
    IMPL.glBindVertexArray(array);
  }

  public static int glGenBuffers() {
    return IMPL.glGenBuffers();
  }

  public static void glBindBuffer(int target, int buffer) {
    IMPL.glBindBuffer(target, buffer);
  }

  public static void glBufferData(int target, float[] data, int count, int usage) {
    IMPL.glBufferData(target, data, count, usage);
  }

  public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
    IMPL.glVertexAttribPointer(index, size, type, normalized, stride, offset);
  }

  public static void glEnableVertexAttribArray(int index) {
    IMPL.glEnableVertexAttribArray(index);
  }

  public static void glDrawArrays(int mode, int first, int count) {
    IMPL.glDrawArrays(mode, first, count);
  }

  public static void glClearColor(float red, float green, float blue, float alpha) {
    IMPL.glClearColor(red, green, blue, alpha);
  }

  public static void glClear(int mask) {
    IMPL.glClear(mask);
  }

  public static void glEnable(int capability) {
    IMPL.glEnable(capability);
  }

  public static void glDisable(int capability) {
    IMPL.glDisable(capability);
  }

  public static void glBlendFunc(int source, int destination) {
    IMPL.glBlendFunc(source, destination);
  }

  public static int glGetInteger(int pname) {
    return IMPL.glGetInteger(pname);
  }

  public static void glGetIntegerv(int pname, int[] params) {
    IMPL.glGetIntegerv(pname, params);
  }

  public static String glGetString(int name) {
    return IMPL.glGetString(name);
  }

  public static int glCreateShader(int type) {
    return IMPL.glCreateShader(type);
  }

  public static void glShaderSource(int shader, CharSequence source) {
    IMPL.glShaderSource(shader, source);
  }

  public static void glCompileShader(int shader) {
    IMPL.glCompileShader(shader);
  }

  public static int glGetShaderi(int shader, int pname) {
    return IMPL.glGetShaderi(shader, pname);
  }

  public static String glGetShaderInfoLog(int shader) {
    return IMPL.glGetShaderInfoLog(shader);
  }

  public static void glDeleteShader(int shader) {
    IMPL.glDeleteShader(shader);
  }

  public static int glCreateProgram() {
    return IMPL.glCreateProgram();
  }

  public static void glAttachShader(int program, int shader) {
    IMPL.glAttachShader(program, shader);
  }

  public static void glLinkProgram(int program) {
    IMPL.glLinkProgram(program);
  }

  public static int glGetProgrami(int program, int pname) {
    return IMPL.glGetProgrami(program, pname);
  }

  public static String glGetProgramInfoLog(int program) {
    return IMPL.glGetProgramInfoLog(program);
  }

  public static void glDeleteProgram(int program) {
    IMPL.glDeleteProgram(program);
  }

  public static void glUseProgram(int program) {
    IMPL.glUseProgram(program);
  }

  public static int glGetUniformLocation(int program, CharSequence name) {
    return IMPL.glGetUniformLocation(program, name);
  }

  public static void glUniform1f(int location, float v0) {
    IMPL.glUniform1f(location, v0);
  }

  public static void glUniform2f(int location, float v0, float v1) {
    IMPL.glUniform2f(location, v0, v1);
  }

  public static void glUniform3f(int location, float v0, float v1, float v2) {
    IMPL.glUniform3f(location, v0, v1, v2);
  }

  public static void glUniform4f(int location, float v0, float v1, float v2, float v3) {
    IMPL.glUniform4f(location, v0, v1, v2, v3);
  }

  public static void glUniform1i(int location, int v0) {
    IMPL.glUniform1i(location, v0);
  }

  public static void glUniform1fv(int location, float[] values) {
    IMPL.glUniform1fv(location, values);
  }

  public static void glUniform2fv(int location, float[] values) {
    IMPL.glUniform2fv(location, values);
  }

  public static void glUniform3fv(int location, float[] values) {
    IMPL.glUniform3fv(location, values);
  }

  public static void glUniform4fv(int location, float[] values) {
    IMPL.glUniform4fv(location, values);
  }

  public static void glUniformMatrix3fv(int location, boolean transpose, float[] values) {
    IMPL.glUniformMatrix3fv(location, transpose, values);
  }
}
