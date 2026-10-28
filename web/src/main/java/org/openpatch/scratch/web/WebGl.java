package org.openpatch.scratch.web;

import org.openpatch.scratch.internal.platform.Gl;
import org.teavm.jso.JSBody;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.jso.typedarrays.TypedArray;

/**
 * The renderer's GL on WebGL2 (s4j.js): handles are ints as in OpenGL, arrays are copied into
 * typed arrays, ARGB ints become RGBA bytes there, GLSL 330 becomes GLSL ES 300.
 */
final class WebGl implements Gl {

  @Override
  public int glGenTextures() {
    return nGenTextures();
  }

  @Override
  public void glDeleteTextures(int texture) {
    nDeleteTextures(texture);
  }

  @Override
  public void glBindTexture(int target, int texture) {
    nBindTexture(target, texture);
  }

  @Override
  public void glActiveTexture(int unit) {
    nActiveTexture(unit);
  }

  @Override
  public void glPixelStorei(int pname, int param) {
    nPixelStorei(pname, param);
  }

  @Override
  public void glTexParameteri(int target, int pname, int param) {
    nTexParameteri(target, pname, param);
  }

  @Override
  public void glGenerateMipmap(int target) {
    nGenerateMipmap(target);
  }

  @Override
  public void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, int[] pixels) {
    nTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels == null ? null : Int32Array.copyFromJavaArray(pixels));
  }

  @Override
  public void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, byte[] pixels) {
    nTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels == null ? null : Int8Array.copyFromJavaArray(pixels));
  }

  @Override
  public void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, int[] pixels) {
    nTexSubImage2D(target, level, x, y, width, height, format, type, pixels == null ? null : Int32Array.copyFromJavaArray(pixels));
  }

  @Override
  public void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, byte[] pixels) {
    nTexSubImage2D(target, level, x, y, width, height, format, type, pixels == null ? null : Int8Array.copyFromJavaArray(pixels));
  }

  @Override
  public int glGenFramebuffers() {
    return nGenFramebuffers();
  }

  @Override
  public void glDeleteFramebuffers(int framebuffer) {
    nDeleteFramebuffers(framebuffer);
  }

  @Override
  public void glBindFramebuffer(int target, int framebuffer) {
    nBindFramebuffer(target, framebuffer);
  }

  @Override
  public void glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
    nFramebufferTexture2D(target, attachment, textureTarget, texture, level);
  }

  @Override
  public int glCheckFramebufferStatus(int target) {
    return nCheckFramebufferStatus(target);
  }

  @Override
  public int glGenRenderbuffers() {
    return nGenRenderbuffers();
  }

  @Override
  public void glDeleteRenderbuffers(int renderbuffer) {
    nDeleteRenderbuffers(renderbuffer);
  }

  @Override
  public void glBindRenderbuffer(int target, int renderbuffer) {
    nBindRenderbuffer(target, renderbuffer);
  }

  @Override
  public void glRenderbufferStorageMultisample(int target, int samples, int internalFormat, int width, int height) {
    nRenderbufferStorageMultisample(target, samples, internalFormat, width, height);
  }

  @Override
  public void glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer) {
    nFramebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer);
  }

  @Override
  public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
    nBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
  }

  @Override
  public void glViewport(int x, int y, int width, int height) {
    nViewport(x, y, width, height);
  }

  @Override
  public void glScissor(int x, int y, int width, int height) {
    nScissor(x, y, width, height);
  }

  @Override
  public void glReadPixels(int x, int y, int width, int height, int format, int type, int[] pixels) {
    int[] read = readPixels(x, y, width, height).copyToJavaArray();
    System.arraycopy(read, 0, pixels, 0, Math.min(read.length, pixels.length));
  }

  @Override
  public int glGenVertexArrays() {
    return nGenVertexArrays();
  }

  @Override
  public void glBindVertexArray(int array) {
    nBindVertexArray(array);
  }

  @Override
  public int glGenBuffers() {
    return nGenBuffers();
  }

  @Override
  public void glBindBuffer(int target, int buffer) {
    nBindBuffer(target, buffer);
  }

  @Override
  public void glBufferData(int target, float[] data, int count, int usage) {
    bufferData(target, Float32Array.copyFromJavaArray(java.util.Arrays.copyOf(data, count)), usage);
  }

  @Override
  public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
    nVertexAttribPointer(index, size, type, normalized, stride, (int) offset);
  }

  @Override
  public void glEnableVertexAttribArray(int index) {
    nEnableVertexAttribArray(index);
  }

  @Override
  public void glDrawArrays(int mode, int first, int count) {
    nDrawArrays(mode, first, count);
  }

  @Override
  public void glClearColor(float red, float green, float blue, float alpha) {
    nClearColor(red, green, blue, alpha);
  }

  @Override
  public void glClear(int mask) {
    nClear(mask);
  }

  @Override
  public void glEnable(int capability) {
    nEnable(capability);
  }

  @Override
  public void glDisable(int capability) {
    nDisable(capability);
  }

  @Override
  public void glBlendFunc(int source, int destination) {
    nBlendFunc(source, destination);
  }

  @Override
  public int glGetInteger(int pname) {
    return nGetInteger(pname);
  }

  @Override
  public void glGetIntegerv(int pname, int[] params) {
    int[] values = getIntegerv(pname).copyToJavaArray();
    System.arraycopy(values, 0, params, 0, Math.min(values.length, params.length));
  }

  @Override
  public String glGetString(int name) {
    return nGetString(name);
  }

  @Override
  public int glCreateShader(int type) {
    return nCreateShader(type);
  }

  @Override
  public void glShaderSource(int shader, CharSequence source) {
    nShaderSource(shader, source.toString());
  }

  @Override
  public void glCompileShader(int shader) {
    nCompileShader(shader);
  }

  @Override
  public int glGetShaderi(int shader, int pname) {
    return nGetShaderi(shader, pname);
  }

  @Override
  public String glGetShaderInfoLog(int shader) {
    return nGetShaderInfoLog(shader);
  }

  @Override
  public void glDeleteShader(int shader) {
    nDeleteShader(shader);
  }

  @Override
  public int glCreateProgram() {
    return nCreateProgram();
  }

  @Override
  public void glAttachShader(int program, int shader) {
    nAttachShader(program, shader);
  }

  @Override
  public void glLinkProgram(int program) {
    nLinkProgram(program);
  }

  @Override
  public int glGetProgrami(int program, int pname) {
    return nGetProgrami(program, pname);
  }

  @Override
  public String glGetProgramInfoLog(int program) {
    return nGetProgramInfoLog(program);
  }

  @Override
  public void glDeleteProgram(int program) {
    nDeleteProgram(program);
  }

  @Override
  public void glUseProgram(int program) {
    nUseProgram(program);
  }

  @Override
  public int glGetUniformLocation(int program, CharSequence name) {
    return nGetUniformLocation(program, name.toString());
  }

  @Override
  public void glUniform1f(int location, float v0) {
    nUniform1f(location, v0);
  }

  @Override
  public void glUniform2f(int location, float v0, float v1) {
    nUniform2f(location, v0, v1);
  }

  @Override
  public void glUniform3f(int location, float v0, float v1, float v2) {
    nUniform3f(location, v0, v1, v2);
  }

  @Override
  public void glUniform4f(int location, float v0, float v1, float v2, float v3) {
    nUniform4f(location, v0, v1, v2, v3);
  }

  @Override
  public void glUniform1i(int location, int v0) {
    nUniform1i(location, v0);
  }

  @Override
  public void glUniform1fv(int location, float[] values) {
    nUniform1fv(location, Float32Array.copyFromJavaArray(values));
  }

  @Override
  public void glUniform2fv(int location, float[] values) {
    nUniform2fv(location, Float32Array.copyFromJavaArray(values));
  }

  @Override
  public void glUniform3fv(int location, float[] values) {
    nUniform3fv(location, Float32Array.copyFromJavaArray(values));
  }

  @Override
  public void glUniform4fv(int location, float[] values) {
    nUniform4fv(location, Float32Array.copyFromJavaArray(values));
  }

  @Override
  public void glUniformMatrix3fv(int location, boolean transpose, float[] values) {
    nUniformMatrix3fv(location, transpose, Float32Array.copyFromJavaArray(values));
  }

  // ---- s4j.js ---------------------------------------------------------------------------------

  @JSBody(script = "return S4J.gl.genTexture();")
  private static native int nGenTextures();

  @JSBody(params = "texture", script = "S4J.gl.deleteTexture(texture);")
  private static native void nDeleteTextures(int texture);

  @JSBody(params = {"target", "texture"}, script = "S4J.gl.bindTexture(target, texture);")
  private static native void nBindTexture(int target, int texture);

  @JSBody(params = "unit", script = "S4J.gl.activeTexture(unit);")
  private static native void nActiveTexture(int unit);

  @JSBody(params = {"pname", "param"}, script = "S4J.gl.pixelStorei(pname, param);")
  private static native void nPixelStorei(int pname, int param);

  @JSBody(params = {"target", "pname", "param"}, script = "S4J.gl.texParameteri(target, pname, param);")
  private static native void nTexParameteri(int target, int pname, int param);

  @JSBody(params = "target", script = "S4J.gl.generateMipmap(target);")
  private static native void nGenerateMipmap(int target);

  @JSBody(params = {"target", "level", "internalFormat", "width", "height", "border", "format", "type", "pixels"}, script = "S4J.gl.texImage2D(target, level, internalFormat, width, height, border, format, type, pixels);")
  private static native void nTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, TypedArray pixels);

  @JSBody(params = {"target", "level", "x", "y", "width", "height", "format", "type", "pixels"}, script = "S4J.gl.texSubImage2D(target, level, x, y, width, height, format, type, pixels);")
  private static native void nTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, TypedArray pixels);

  @JSBody(script = "return S4J.gl.genFramebuffer();")
  private static native int nGenFramebuffers();

  @JSBody(params = "framebuffer", script = "S4J.gl.deleteFramebuffer(framebuffer);")
  private static native void nDeleteFramebuffers(int framebuffer);

  @JSBody(params = {"target", "framebuffer"}, script = "S4J.gl.bindFramebuffer(target, framebuffer);")
  private static native void nBindFramebuffer(int target, int framebuffer);

  @JSBody(params = {"target", "attachment", "textureTarget", "texture", "level"}, script = "S4J.gl.framebufferTexture2D(target, attachment, textureTarget, texture, level);")
  private static native void nFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level);

  @JSBody(params = "target", script = "return S4J.gl.checkFramebufferStatus(target);")
  private static native int nCheckFramebufferStatus(int target);

  @JSBody(script = "return S4J.gl.genRenderbuffer();")
  private static native int nGenRenderbuffers();

  @JSBody(params = "renderbuffer", script = "S4J.gl.deleteRenderbuffer(renderbuffer);")
  private static native void nDeleteRenderbuffers(int renderbuffer);

  @JSBody(params = {"target", "renderbuffer"}, script = "S4J.gl.bindRenderbuffer(target, renderbuffer);")
  private static native void nBindRenderbuffer(int target, int renderbuffer);

  @JSBody(params = {"target", "samples", "internalFormat", "width", "height"}, script = "S4J.gl.renderbufferStorageMultisample(target, samples, internalFormat, width, height);")
  private static native void nRenderbufferStorageMultisample(int target, int samples, int internalFormat, int width, int height);

  @JSBody(params = {"target", "attachment", "renderbufferTarget", "renderbuffer"}, script = "S4J.gl.framebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer);")
  private static native void nFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer);

  @JSBody(params = {"srcX0", "srcY0", "srcX1", "srcY1", "dstX0", "dstY0", "dstX1", "dstY1", "mask", "filter"}, script = "S4J.gl.blitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);")
  private static native void nBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter);

  @JSBody(params = {"x", "y", "width", "height"}, script = "S4J.gl.viewport(x, y, width, height);")
  private static native void nViewport(int x, int y, int width, int height);

  @JSBody(params = {"x", "y", "width", "height"}, script = "S4J.gl.scissor(x, y, width, height);")
  private static native void nScissor(int x, int y, int width, int height);

  @JSBody(params = {"x", "y", "width", "height"}, script = "return S4J.gl.readPixels(x, y, width, height);")
  private static native Int32Array readPixels(int x, int y, int width, int height);

  @JSBody(script = "return S4J.gl.genVertexArray();")
  private static native int nGenVertexArrays();

  @JSBody(params = "array", script = "S4J.gl.bindVertexArray(array);")
  private static native void nBindVertexArray(int array);

  @JSBody(script = "return S4J.gl.genBuffer();")
  private static native int nGenBuffers();

  @JSBody(params = {"target", "buffer"}, script = "S4J.gl.bindBuffer(target, buffer);")
  private static native void nBindBuffer(int target, int buffer);

  @JSBody(params = {"target", "data", "usage"}, script = "S4J.gl.bufferData(target, data, usage);")
  private static native void bufferData(int target, Float32Array data, int usage);

  @JSBody(params = {"index", "size", "type", "normalized", "stride", "offset"}, script = "S4J.gl.vertexAttribPointer(index, size, type, normalized, stride, offset);")
  private static native void nVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, int offset);

  @JSBody(params = "index", script = "S4J.gl.enableVertexAttribArray(index);")
  private static native void nEnableVertexAttribArray(int index);

  @JSBody(params = {"mode", "first", "count"}, script = "S4J.gl.drawArrays(mode, first, count);")
  private static native void nDrawArrays(int mode, int first, int count);

  @JSBody(params = {"red", "green", "blue", "alpha"}, script = "S4J.gl.clearColor(red, green, blue, alpha);")
  private static native void nClearColor(float red, float green, float blue, float alpha);

  @JSBody(params = "mask", script = "S4J.gl.clear(mask);")
  private static native void nClear(int mask);

  @JSBody(params = "capability", script = "S4J.gl.enable(capability);")
  private static native void nEnable(int capability);

  @JSBody(params = "capability", script = "S4J.gl.disable(capability);")
  private static native void nDisable(int capability);

  @JSBody(params = {"source", "destination"}, script = "S4J.gl.blendFunc(source, destination);")
  private static native void nBlendFunc(int source, int destination);

  @JSBody(params = "pname", script = "return S4J.gl.getInteger(pname);")
  private static native int nGetInteger(int pname);

  @JSBody(params = "pname", script = "return S4J.gl.getIntegerv(pname);")
  private static native Int32Array getIntegerv(int pname);

  @JSBody(params = "name", script = "return S4J.gl.getString(name);")
  private static native String nGetString(int name);

  @JSBody(params = "type", script = "return S4J.gl.createShader(type);")
  private static native int nCreateShader(int type);

  @JSBody(params = {"shader", "source"}, script = "S4J.gl.shaderSource(shader, source);")
  private static native void nShaderSource(int shader, String source);

  @JSBody(params = "shader", script = "S4J.gl.compileShader(shader);")
  private static native void nCompileShader(int shader);

  @JSBody(params = {"shader", "pname"}, script = "return S4J.gl.getShaderi(shader, pname);")
  private static native int nGetShaderi(int shader, int pname);

  @JSBody(params = "shader", script = "return S4J.gl.getShaderInfoLog(shader);")
  private static native String nGetShaderInfoLog(int shader);

  @JSBody(params = "shader", script = "S4J.gl.deleteShader(shader);")
  private static native void nDeleteShader(int shader);

  @JSBody(script = "return S4J.gl.createProgram();")
  private static native int nCreateProgram();

  @JSBody(params = {"program", "shader"}, script = "S4J.gl.attachShader(program, shader);")
  private static native void nAttachShader(int program, int shader);

  @JSBody(params = "program", script = "S4J.gl.linkProgram(program);")
  private static native void nLinkProgram(int program);

  @JSBody(params = {"program", "pname"}, script = "return S4J.gl.getProgrami(program, pname);")
  private static native int nGetProgrami(int program, int pname);

  @JSBody(params = "program", script = "return S4J.gl.getProgramInfoLog(program);")
  private static native String nGetProgramInfoLog(int program);

  @JSBody(params = "program", script = "S4J.gl.deleteProgram(program);")
  private static native void nDeleteProgram(int program);

  @JSBody(params = "program", script = "S4J.gl.useProgram(program);")
  private static native void nUseProgram(int program);

  @JSBody(params = {"program", "name"}, script = "return S4J.gl.getUniformLocation(program, name);")
  private static native int nGetUniformLocation(int program, String name);

  @JSBody(params = {"location", "v0"}, script = "if (location >= 0) S4J.gl.uniform1f(location, v0);")
  private static native void nUniform1f(int location, float v0);

  @JSBody(params = {"location", "v0", "v1"}, script = "if (location >= 0) S4J.gl.uniform2f(location, v0, v1);")
  private static native void nUniform2f(int location, float v0, float v1);

  @JSBody(params = {"location", "v0", "v1", "v2"}, script = "if (location >= 0) S4J.gl.uniform3f(location, v0, v1, v2);")
  private static native void nUniform3f(int location, float v0, float v1, float v2);

  @JSBody(params = {"location", "v0", "v1", "v2", "v3"}, script = "if (location >= 0) S4J.gl.uniform4f(location, v0, v1, v2, v3);")
  private static native void nUniform4f(int location, float v0, float v1, float v2, float v3);

  @JSBody(params = {"location", "v0"}, script = "if (location >= 0) S4J.gl.uniform1i(location, v0);")
  private static native void nUniform1i(int location, int v0);

  @JSBody(params = {"location", "values"}, script = "if (location >= 0) S4J.gl.uniform1fv(location, values);")
  private static native void nUniform1fv(int location, Float32Array values);

  @JSBody(params = {"location", "values"}, script = "if (location >= 0) S4J.gl.uniform2fv(location, values);")
  private static native void nUniform2fv(int location, Float32Array values);

  @JSBody(params = {"location", "values"}, script = "if (location >= 0) S4J.gl.uniform3fv(location, values);")
  private static native void nUniform3fv(int location, Float32Array values);

  @JSBody(params = {"location", "values"}, script = "if (location >= 0) S4J.gl.uniform4fv(location, values);")
  private static native void nUniform4fv(int location, Float32Array values);

  @JSBody(params = {"location", "transpose", "values"}, script = "if (location >= 0) S4J.gl.uniformMatrix3fv(location, transpose, values);")
  private static native void nUniformMatrix3fv(int location, boolean transpose, Float32Array values);
}
