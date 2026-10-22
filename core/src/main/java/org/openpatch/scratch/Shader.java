package org.openpatch.scratch;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.openpatch.scratch.internal.draw.ShaderDraw;
import org.openpatch.scratch.internal.image.Images;

/**
 * A shader: a small program the graphics card runs for every pixel of a sprite (or of the whole
 * stage) to change how it looks. You write only the body; every shader gets the same header:
 *
 * <pre>{@code
 * uniform sampler2D costume;   // the costume (or the stage's picture)
 * uniform vec2 resolution;     // its size in pixels
 * uniform float time;          // game time in seconds
 * in vec2 uv;                  // where in the costume: (0, 0) top left, (1, 1) bottom right
 * out vec4 color;              // the colour to draw
 * }</pre>
 *
 * <pre>{@code
 * void main() {
 *   vec4 c = texture(costume, uv);
 *   float g = (c.r + c.g + c.b) / 3.0;
 *   color = vec4(g, g, g, c.a);
 * }
 * }</pre>
 *
 * <p>Colours have straight (not premultiplied) alpha. Values of your own come in through
 * uniforms you declare and set with {@link #set(String, double)} and friends; the header's
 * {@code time} and {@code resolution} are filled in for you and cannot be set.
 */
public final class Shader {

  /** Names the header declares: setting them is an error. */
  private static final Set<String> HEADER = Set.of("costume", "resolution", "time", "uv", "color");

  private String name;
  private final String path;
  private final String source;
  private final Map<String, Object> uniforms = new LinkedHashMap<>();

  Shader(String name, String fragmentPath) {
    this.name = name;
    this.path = fragmentPath;
    byte[] bytes = Images.read(fragmentPath);
    if (bytes == null) {
      throw new IllegalArgumentException("Shader not found: " + fragmentPath);
    }
    this.source = new String(bytes, StandardCharsets.UTF_8);
  }

  Shader(Shader shader) {
    this.name = shader.name;
    this.path = shader.path;
    this.source = shader.source;
    this.uniforms.putAll(shader.uniforms);
  }

  /** @return the shader's name */
  public String getName() {
    return this.name;
  }

  /** @param name a new name */
  public void setName(String name) {
    this.name = name;
  }

  private void put(String uniform, Object value) {
    if (HEADER.contains(uniform)) {
      throw new IllegalArgumentException("\"" + uniform + "\" is filled in by Scratch for Java; remove the line "
          + "that sets it, or use a uniform of your own with another name.");
    }
    synchronized (this.uniforms) {
      this.uniforms.put(uniform, value);
    }
  }

  /**
   * @param uniform the uniform's name in the shader
   * @param value an {@code int} or {@code float} uniform's value
   *
   * @example.files ShaderSet.java
   */
  public void set(String uniform, double value) {
    this.put(uniform, new float[] {(float) value});
  }

  /**
   * @param uniform the uniform's name
   * @param value an {@code int} uniform's value
   *
   * @example.files ShaderSet.java
   */
  public void set(String uniform, int value) {
    this.put(uniform, new int[] {value});
  }

  /**
   * @param uniform the uniform's name
   * @param value a {@code bool} uniform's value
   *
   * @example.files ShaderSet.java
   */
  public void set(String uniform, boolean value) {
    this.put(uniform, new int[] {value ? 1 : 0});
  }

  /**
   * @param uniform the uniform's name
   * @param x a {@code vec2}'s x
   * @param y a {@code vec2}'s y
   *
   * @example.files ShaderSet.java
   */
  public void set(String uniform, double x, double y) {
    this.put(uniform, new float[] {(float) x, (float) y});
  }

  /**
   * @param uniform the uniform's name
   * @param value a {@code vec2}
   *
   * @example.files ShaderSet.java
   */
  public void set(String uniform, Vector2 value) {
    this.set(uniform, value.getX(), value.getY());
  }

  /**
   * @param uniform the uniform's name
   * @param color a {@code vec4}: red, green, blue and alpha from 0 to 1
   *
   * @example.files ShaderSet.java
   */
  public void set(String uniform, Color color) {
    this.put(uniform, new float[] {(float) (color.getRed() / 255), (float) (color.getGreen() / 255),
        (float) (color.getBlue() / 255), (float) (color.getAlpha() / 255)});
  }

  /**
   * An array uniform such as {@code uniform vec3 lights[100];}.
   *
   * @param uniform the uniform's name
   * @param values all values, one element after the other
   * @param perElement values per element: 1 for {@code float[]}, 3 for {@code vec3[]}
   *
   * @example.files ShaderSet.java
   */
  public void set(String uniform, double[] values, int perElement) {
    float[] f = new float[values.length + 1];
    f[0] = perElement;
    for (int i = 0; i < values.length; i++) {
      f[i + 1] = (float) values[i];
    }
    this.put(uniform + "[]", f);
  }

  /** The body and a copy of the uniforms, for the draw list. */
  ShaderDraw toDraw() {
    synchronized (this.uniforms) {
      Map<String, Object> copy = new LinkedHashMap<>();
      this.uniforms.forEach((k, v) -> copy.put(k, v instanceof float[] f ? f.clone() : ((int[]) v).clone()));
      return new ShaderDraw(this.path, this.source, copy);
    }
  }
}
