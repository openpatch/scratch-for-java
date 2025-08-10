package org.openpatch.scratch.extensions.shader;

import java.util.concurrent.TimeUnit;
import org.openpatch.scratch.extensions.color.Color;
import org.openpatch.scratch.extensions.math.Vector2;
import org.openpatch.scratch.internal.Applet;
import processing.opengl.PShader;
import processing.opengl.PSurfaceJOGL;

public class Shader {

  private String name;
  private PShader shader;

  /**
   * Constructor
   *
   * @param name unique name
   * @param path path to the shader file
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
    vertexShaderPath = vertexShaderPath.replaceFirst("^~", System.getProperty("user.home"));
    fragementShaderPath = fragementShaderPath.replaceFirst("^~", System.getProperty("user.home"));
    var surface = (PSurfaceJOGL) Applet.getInstance().getSurface();

    // The surface context may not be initialized yet.
    // The surface is initialized in the Applet constructor, but in another thread.
    while (surface.pgl.context == null) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return Applet.getInstance().loadShader(fragementShaderPath, vertexShaderPath);
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
