package spike;

import static org.lwjgl.opengl.GL33C.*;

/** Compiles and links a vertex/fragment pair, with the driver's message on failure. */
final class SpikeShaders {

  private SpikeShaders() {}

  static int program(String vertex, String fragment) {
    int program = glCreateProgram();
    glAttachShader(program, compile(GL_VERTEX_SHADER, vertex));
    glAttachShader(program, compile(GL_FRAGMENT_SHADER, fragment));
    glLinkProgram(program);
    if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
      throw new IllegalStateException("does not link:\n" + glGetProgramInfoLog(program));
    }
    return program;
  }

  private static int compile(int type, String source) {
    int shader = glCreateShader(type);
    glShaderSource(shader, source);
    glCompileShader(shader);
    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
      throw new IllegalStateException("does not compile:\n" + glGetShaderInfoLog(shader));
    }
    return shader;
  }
}
