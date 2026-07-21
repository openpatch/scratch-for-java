package org.openpatch.scratch.extensions.shader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The shaders belonging to one sprite or stage, and which of them is currently
 * drawn with.
 *
 * <p>
 * Reached through {@code getShaders()}, so that shader handling does not take up
 * room in the everyday API of a sprite:
 *
 * <pre>{@code
 * this.getShaders().add("blur", "blur.frag", null);
 * this.getShaders().switchTo("blur");
 * }</pre>
 */
public class Shaders {

  private final List<Shader> shaders = new CopyOnWriteArrayList<>();
  private int current = 0;
  private final String owner;

  /**
   * Creates an empty set of shaders.
   *
   * @param owner what the shaders belong to, used in error messages: "sprite" or
   *              "stage"
   */
  public Shaders(String owner) {
    this.owner = owner;
  }

  /**
   * Copies a set of shaders.
   *
   * @param s the shaders to copy
   */
  public Shaders(Shaders s) {
    this.owner = s.owner;
    this.current = s.current;
    for (Shader shader : s.shaders) {
      this.shaders.add(new Shader(shader));
    }
  }

  /**
   * Adds a shader. If a shader with the given name already exists, that one is
   * returned instead.
   *
   * @param name               a unique name
   * @param fragmentShaderPath the path to the fragment shader
   * @param vertexShaderPath   the path to the vertex shader, may be null
   * @return the shader
   */
  public Shader add(String name, final String fragmentShaderPath, final String vertexShaderPath) {
    for (Shader shader : this.shaders) {
      if (shader.getName().equals(name)) {
        return shader;
      }
    }

    Shader shader = new Shader(name, fragmentShaderPath, vertexShaderPath);
    this.shaders.add(shader);
    return shader;
  }

  /**
   * Returns a shader by name.
   *
   * @param name the name of the shader
   * @return the shader, or null if there is none with that name
   */
  public Shader get(String name) {
    for (Shader shader : this.shaders) {
      if (shader.getName().equals(name)) {
        return shader;
      }
    }
    return null;
  }

  /**
   * Switches to the shader with the given name.
   *
   * @param name the name of the shader
   */
  public void switchTo(String name) {
    for (int i = 0; i < this.shaders.size(); i++) {
      Shader shader = this.shaders.get(i);
      if (shader.getName().equals(name)) {
        this.current = i;
        return;
      }
    }

    System.err.println("\n==============================================");
    System.err.println("WARNING: Shader not found!");
    System.err.println("==============================================");
    System.err.println("Shader name: '" + name + "'");
    if (this.shaders.isEmpty()) {
      System.err.println("\nThis " + this.owner + " has no shaders.");
      System.err.println("\nTip: Use getShaders().add() to add a shader first.");
    } else {
      System.err.println("\nAvailable shaders:");
      for (Shader shader : this.shaders) {
        System.err.println("  - '" + shader.getName() + "'");
      }
      System.err.println("\nTip: Check the spelling of your shader name.");
    }
    System.err.println("==============================================\n");
  }

  /**
   * Switches to the shader at the given position.
   *
   * @param index the position of the shader
   */
  public void switchTo(double index) {
    if (this.shaders.isEmpty()) {
      return;
    }
    this.current = (int) index % this.shaders.size();
  }

  /** Switches to the next shader. */
  public void next() {
    if (this.shaders.isEmpty()) {
      return;
    }
    this.current = (this.current + 1) % this.shaders.size();
  }

  /** Stops drawing with a shader. */
  public void reset() {
    this.current = -1;
  }

  /**
   * Returns the shader currently drawn with.
   *
   * @return the current shader, or null if there is none
   */
  public Shader getCurrent() {
    if (this.shaders.isEmpty() || this.current == -1) {
      return null;
    }
    return this.shaders.get(this.current);
  }

  /**
   * Returns the position of the current shader.
   *
   * @return the position, or -1 if no shader is in use
   */
  public int getCurrentIndex() {
    return this.current;
  }

  /**
   * Returns the name of the current shader.
   *
   * @return the name, or null if no shader is in use
   */
  public String getCurrentName() {
    Shader shader = this.getCurrent();
    return shader == null ? null : shader.getName();
  }
}
