package org.openpatch.scratch.extensions.shader;

import java.util.concurrent.TimeUnit;
import org.openpatch.scratch.Color;
import org.openpatch.scratch.Vector2;
import org.openpatch.scratch.ScratchException;
import org.openpatch.scratch.internal.Applet;

import processing.opengl.PShader;

/**
 * A fragment shader a sprite or the stage can be drawn with.
 *
 * <p>
 * A shader is a small program in GLSL, the language of the graphics card. It is
 * run once for every pixel and decides the pixel's colour. Processing hands it
 * the picture being drawn as {@code texture}, and where on that picture the
 * pixel is as {@code vertTexCoord}:
 *
 * <pre>{@code
 * #ifdef GL_ES
 * precision mediump float;
 * #endif
 *
 * uniform sampler2D texture;
 * varying vec4 vertTexCoord;
 *
 * void main(void) {
 *   vec4 color = texture2D(texture, vertTexCoord.st);
 *   float grey = (color.r + color.g + color.b) / 3.0;
 *   gl_FragColor = vec4(grey, grey, grey, color.a);
 * }
 * }</pre>
 *
 * <p>
 * A shader is not made with {@code new} but added to a sprite or the stage with
 * {@code getShaders().add(...)}, which returns it. Its {@code uniform} values
 * are given from Java with {@link #set(String, int)}.
 *
 * <p>
 * In the browser the shader is translated for WebGL, which is stricter than a
 * desktop graphics card: a float has to be written as {@code 1.0}, not
 * {@code 1}. A vertex shader other than Processing's default one is ignored
 * there.
 */
public class Shader {

  private String name;
  private PShader shader;

  /**
   * Constructor
   *
   * @param name               unique name
   * @param fragmentShaderPath path to the fragment shader file
   * @param vertexShaderPath   path to the vertex shader file, or null for Processing's default one
   */
  public Shader(String name, String fragmentShaderPath, String vertexShaderPath) {
    this.name = name;
    this.shader = loadPShader(fragmentShaderPath, vertexShaderPath);
  }

  /**
   * Copy constructor
   *
   * @param shader shader
   */
  public Shader(Shader shader) {
    this.name = shader.getName();
    this.shader = shader.getPShader();
  }

  /**
   * Load shader
   *
   * @param path path to the shader file
   * @return shader
   */
  private static PShader loadPShader(String fragementShaderPath, String vertexShaderPath) {
    // Without a vertex shader, Processing uses its default one.
    if (vertexShaderPath != null) {
      vertexShaderPath = vertexShaderPath.replaceFirst("^~", System.getProperty("user.home"));
    }
    fragementShaderPath = fragementShaderPath.replaceFirst("^~", System.getProperty("user.home"));

    // The surface context may not be initialized yet.
    // The surface is initialized in the Applet constructor, but in another thread.
    while (!Applet.getInstance().isSetup()) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    try {
      var shader = vertexShaderPath == null
          ? Applet.getInstance().loadShader(fragementShaderPath)
          : Applet.getInstance().loadShader(fragementShaderPath, vertexShaderPath);
      return shader;
    } catch (Exception e) {
      System.err.println("\n==============================================");
      System.err.println("ERROR: Could not load shader files!");
      System.err.println("==============================================");
      System.err.println("Fragment shader: " + fragementShaderPath);
      System.err.println("Vertex shader:   " + vertexShaderPath);
      System.err.println("\nPossible reasons:");
      System.err.println("  1. One or both files do not exist");
      System.err.println("  2. The file paths are incorrect (check spelling)");
      System.err.println("  3. The shader code contains syntax errors");
      System.err.println("\nTip: Make sure both shader files exist and");
      System.err.println("     the paths are correct.");
      System.err.println("==============================================\n");
      throw new ScratchException("Could not load shader: " + fragementShaderPath);
    }
  }

  /**
   * Returns the name
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name
   *
   * @param name unique name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the shader
   *
   * @return the shader
   */
  public PShader getPShader() {
    return this.shader;
  }

  /**
   * Sets a {@code uniform} of the shader: a value the shader reads but the
   * program decides, such as the time for an animation or a position on the
   * stage.
   *
   * <p>
   * The name is the one the shader file declares, {@code uniform float blocks;}
   * is set with {@code set("blocks", 20)}. There is a {@code set} for one or two
   * numbers or booleans, a {@link org.openpatch.scratch.Vector2}, a
   * {@link org.openpatch.scratch.Color} (as a {@code vec3}), and an array of
   * numbers, {@code ncoords} at a time, for an array uniform like
   * {@code uniform vec3 lights[10];}.
   *
   * <p>
   * A uniform keeps its value until it is set again, so a value that changes is
   * set in {@code run()}.
   *
   * @param name the name of the uniform in the shader file
   * @param x    the value
   *
   * @example.files ShaderSet.java
   */
  public void set(String name, int x) {
    this.shader.set(name, x);
  }

  public void set(String name, boolean x) {
    this.shader.set(name, x);
  }

  public void set(String name, double x) {
    this.shader.set(name, (float) x);
  }

  public void set(String name, int x, int y) {
    this.shader.set(name, x, y);
  }

  public void set(String name, boolean x, boolean y) {
    this.shader.set(name, x, y);
  }

  public void set(String name, double x, double y) {
    this.shader.set(name, (float) x, (float) y);
  }

  public void set(String name, Vector2 vec) {
    this.shader.set(name, (float) vec.getX(), (float) vec.getY());
  }

  public void set(String name, int[] values, int ncoords) {
    this.shader.set(name, values, ncoords);
  }

  public void set(String name, double[] values, int ncoords) {
    float[] fvalues = new float[values.length];
    for (int i = 0; i < values.length; i++) {
      fvalues[i] = (float) values[i];
    }
    this.shader.set(name, fvalues, ncoords);
  }

  public void set(String name, Color c) {
    this.shader.set(name, (float) c.getRed(), (float) c.getGreen(), (float) c.getBlue());
  }
}
