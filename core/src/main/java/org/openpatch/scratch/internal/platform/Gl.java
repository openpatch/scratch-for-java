package org.openpatch.scratch.internal.platform;

/**
 * The OpenGL 3.3 core calls the renderer makes, as LWJGL's GL33C names them; WebGL2 has every one.
 * Pixels and vertices are Java arrays: the platform turns them into what its GL takes.
 * {@link #glBufferData} uploads the first {@code count} floats. Called through {@link GL}.
 */
public interface Gl {

  int glGenTextures();

  void glDeleteTextures(int texture);

  void glBindTexture(int target, int texture);

  void glActiveTexture(int unit);

  void glPixelStorei(int pname, int param);

  void glTexParameteri(int target, int pname, int param);

  void glGenerateMipmap(int target);

  void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, int[] pixels);

  void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, byte[] pixels);

  void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, int[] pixels);

  void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, byte[] pixels);

  int glGenFramebuffers();

  void glDeleteFramebuffers(int framebuffer);

  void glBindFramebuffer(int target, int framebuffer);

  void glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level);

  int glCheckFramebufferStatus(int target);

  int glGenRenderbuffers();

  void glDeleteRenderbuffers(int renderbuffer);

  void glBindRenderbuffer(int target, int renderbuffer);

  void glRenderbufferStorageMultisample(int target, int samples, int internalFormat, int width, int height);

  void glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer);

  void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter);

  void glViewport(int x, int y, int width, int height);

  void glScissor(int x, int y, int width, int height);

  void glReadPixels(int x, int y, int width, int height, int format, int type, int[] pixels);

  int glGenVertexArrays();

  void glBindVertexArray(int array);

  int glGenBuffers();

  void glBindBuffer(int target, int buffer);

  void glBufferData(int target, float[] data, int count, int usage);

  void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset);

  void glEnableVertexAttribArray(int index);

  void glDrawArrays(int mode, int first, int count);

  void glClearColor(float red, float green, float blue, float alpha);

  void glClear(int mask);

  void glEnable(int capability);

  void glDisable(int capability);

  void glBlendFunc(int source, int destination);

  int glGetInteger(int pname);

  void glGetIntegerv(int pname, int[] params);

  String glGetString(int name);

  int glCreateShader(int type);

  void glShaderSource(int shader, CharSequence source);

  void glCompileShader(int shader);

  int glGetShaderi(int shader, int pname);

  String glGetShaderInfoLog(int shader);

  void glDeleteShader(int shader);

  int glCreateProgram();

  void glAttachShader(int program, int shader);

  void glLinkProgram(int program);

  int glGetProgrami(int program, int pname);

  String glGetProgramInfoLog(int program);

  void glDeleteProgram(int program);

  void glUseProgram(int program);

  int glGetUniformLocation(int program, CharSequence name);

  void glUniform1f(int location, float v0);

  void glUniform2f(int location, float v0, float v1);

  void glUniform3f(int location, float v0, float v1, float v2);

  void glUniform4f(int location, float v0, float v1, float v2, float v3);

  void glUniform1i(int location, int v0);

  void glUniform1fv(int location, float[] values);

  void glUniform2fv(int location, float[] values);

  void glUniform3fv(int location, float[] values);

  void glUniform4fv(int location, float[] values);

  void glUniformMatrix3fv(int location, boolean transpose, float[] values);
}
