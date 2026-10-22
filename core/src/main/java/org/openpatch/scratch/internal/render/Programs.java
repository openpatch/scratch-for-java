package org.openpatch.scratch.internal.render;

import static org.lwjgl.opengl.GL33C.*;

import java.util.HashMap;
import java.util.Map;

/**
 * The GLSL programs of the renderer. Textures hold premultiplied alpha, so smooth filtering leaves
 * no dark fringes; student shaders see straight alpha, as the 7.0 shader format promises: their
 * {@code texture()} is wrapped, and their {@code color} is premultiplied after their body.
 * Window thread only.
 */
final class Programs {

  /** Positions in pixels of the target, through a 3×3 matrix into clip space. */
  static final String VERTEX = """
      #version 330 core
      layout(location = 0) in vec2 position;
      layout(location = 1) in vec2 texCoord;
      layout(location = 2) in vec4 vertexColor;
      uniform mat3 transform;
      out vec2 uv;
      out vec4 tint;
      void main() {
        gl_Position = vec4((transform * vec3(position, 1.0)).xy, 0.0, 1.0);
        uv = texCoord;
        tint = vertexColor;
      }
      """;

  /**
   * The graphic effects, the same maths as {@code Effects.apply} on the CPU: hue turned in HSV,
   * then tint, then ghost. On straight colours.
   */
  static final String EFFECTS = """
      uniform float colorEffect;   // turns, 0..1
      uniform vec4 effectTint;     // multiplies, straight
      uniform float ghost;         // how much stays, 0..1
      vec3 _rgb2hsv(vec3 c) {
        vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
        vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
        vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
        float d = q.x - min(q.w, q.y);
        float e = 1.0e-10;
        return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
      }
      vec3 _hsv2rgb(vec3 c) {
        vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
        vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
        return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
      }
      vec4 effects(vec4 c) {
        if (c.a <= 0.0) return c;
        if (colorEffect != 0.0) {
          vec3 hsv = _rgb2hsv(c.rgb);
          if (hsv.z < 0.055) {
            hsv = vec3(0.0, 1.0, 0.055);
          } else if (hsv.y < 0.09) {
            hsv = vec3(0.0, 0.09, hsv.z);
          }
          hsv.x = fract(hsv.x + colorEffect);
          c.rgb = _hsv2rgb(hsv);
        }
        c *= effectTint;
        c.a *= ghost;
        return c;
      }
      """;

  /** Textured quads: premultiplied in, effects on the straight colour, premultiplied out. */
  static final String SPRITE = """
      #version 330 core
      uniform sampler2D image;
      in vec2 uv;
      in vec4 tint;
      out vec4 fragColor;
      """ + EFFECTS + """
      void main() {
        vec4 p = texture(image, uv) * tint;
        vec4 c = p.a > 0.0 ? vec4(p.rgb / p.a, p.a) : vec4(0.0);
        c = effects(c);
        fragColor = vec4(c.rgb * c.a, c.a);
      }
      """;

  /** Solid shapes and glyph coverage: the vertex colour (premultiplied) times the texture's red. */
  static final String COVERAGE = """
      #version 330 core
      uniform sampler2D image;
      uniform bool useTexture;
      in vec2 uv;
      in vec4 tint;
      out vec4 fragColor;
      void main() {
        float coverage = useTexture ? texture(image, uv).r : 1.0;
        fragColor = tint * coverage;
      }
      """;

  /**
   * What every student shader gets in front of its body (decision log, Phase 2). Its
   * {@code main} becomes {@code _student}; a wrapper premultiplies its {@code color} and applies
   * the sprite's effects after it.
   */
  static String studentFragment(String body) {
    return """
        #version 330 core
        uniform sampler2D costume;
        uniform vec2 resolution;
        uniform float time;
        in vec2 uv;
        in vec4 tint;
        out vec4 fragColor;
        vec4 color;
        vec4 _straight(vec4 p) { return p.a > 0.0 ? vec4(p.rgb / p.a, p.a) : vec4(0.0); }
        #define texture(s, p) _straight(texture(s, p))
        """ + EFFECTS + """
        #define main _student
        #line 1
        """ + body + """

        #undef main
        void main() {
          color = vec4(0.0);
          _student();
          vec4 c = effects(color);
          fragColor = vec4(c.rgb * c.a, c.a);
        }
        """;
  }

  /** A transition: {@code from}, {@code to}, {@code progress}; the built-ins in the same format. */
  static String transitionFragment(String body) {
    return """
        #version 330 core
        uniform sampler2D from;
        uniform sampler2D to;
        uniform float progress;
        uniform vec2 resolution;
        in vec2 uv;
        in vec4 tint;
        out vec4 fragColor;
        vec4 color;
        vec4 _straight(vec4 p) { return p.a > 0.0 ? vec4(p.rgb / p.a, p.a) : vec4(0.0); }
        #define texture(s, p) _straight(texture(s, p))
        #define main _transition
        #line 1
        """ + body + """

        #undef main
        void main() {
          color = vec4(0.0, 0.0, 0.0, 1.0);
          _transition();
          fragColor = vec4(color.rgb * color.a, color.a);
        }
        """;
  }

  static final Map<String, String> BUILT_IN_TRANSITIONS = Map.of(
      "fade", """
          void main() {
            float p = progress * 2.0;
            color = p < 1.0 ? mix(texture(from, uv), vec4(0.0, 0.0, 0.0, 1.0), p)
                            : mix(vec4(0.0, 0.0, 0.0, 1.0), texture(to, uv), p - 1.0);
          }
          """,
      "crossfade", """
          void main() {
            color = mix(texture(from, uv), texture(to, uv), progress);
          }
          """,
      "slide-left", """
          void main() {
            vec2 p = uv + vec2(progress, 0.0);
            color = p.x < 1.0 ? texture(from, p) : texture(to, p - vec2(1.0, 0.0));
          }
          """,
      "slide-right", """
          void main() {
            vec2 p = uv - vec2(progress, 0.0);
            color = p.x >= 0.0 ? texture(from, p) : texture(to, p + vec2(1.0, 0.0));
          }
          """,
      "slide-up", """
          void main() {
            vec2 p = uv + vec2(0.0, progress);
            color = p.y < 1.0 ? texture(from, p) : texture(to, p - vec2(0.0, 1.0));
          }
          """,
      "slide-down", """
          void main() {
            vec2 p = uv - vec2(0.0, progress);
            color = p.y >= 0.0 ? texture(from, p) : texture(to, p + vec2(0.0, 1.0));
          }
          """);

  /** A compiled program and its uniform locations, looked up once. */
  static final class Program {
    final int id;
    private final Map<String, Integer> locations = new HashMap<>();

    Program(int id) {
      this.id = id;
    }

    int uniform(String name) {
      return this.locations.computeIfAbsent(name, n -> glGetUniformLocation(this.id, n));
    }
  }

  /** Compiles and links; the message names the shader and keeps the student's line numbers. */
  static Program link(String name, String vertex, String fragment) {
    int program = glCreateProgram();
    int v = compile(GL_VERTEX_SHADER, vertex, name);
    int f = compile(GL_FRAGMENT_SHADER, fragment, name);
    glAttachShader(program, v);
    glAttachShader(program, f);
    glLinkProgram(program);
    glDeleteShader(v);
    glDeleteShader(f);
    if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
      String log = glGetProgramInfoLog(program);
      glDeleteProgram(program);
      throw new ShaderException(name + " does not link:\n" + log);
    }
    return new Program(program);
  }

  private static int compile(int type, String source, String name) {
    int shader = glCreateShader(type);
    glShaderSource(shader, source);
    glCompileShader(shader);
    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
      String log = glGetShaderInfoLog(shader);
      glDeleteShader(shader);
      throw new ShaderException(name + " does not compile:\n" + log);
    }
    return shader;
  }

  /** A shader a learner wrote does not compile: shown once, the sprite is drawn without it. */
  static final class ShaderException extends RuntimeException {
    ShaderException(String message) {
      super(message);
    }
  }

  private Programs() {}
}
