package org.openpatch.scratch.extensions.shader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The shaders belonging to one sprite or stage, and which of them are
 * currently drawn with.
 *
 * <p>
 * Reached through {@code getShaders()}, so that shader handling does not take up
 * room in the everyday API of a sprite:
 *
 * <pre>{@code
 * this.getShaders().add("blur", "blur.frag", null);
 * this.getShaders().switchTo("blur");
 * }</pre>
 *
 * <p>
 * {@link #switchTo(String)} draws with exactly one shader. A stage can also
 * draw with several at once, like Scratch's graphic effects:
 * {@link #activate(String)} adds one to those already active, and they take
 * turns in the order they were added, each working on what the one before it
 * drew. A sprite draws with the first of its active shaders.
 */
public class Shaders {

  private final List<Shader> shaders = new CopyOnWriteArrayList<>();
  private final Set<Shader> active = new CopyOnWriteArraySet<>();
  // The first shader added is drawn with at once, unless reset() came first.
  private boolean activateFirst = true;
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
    this.activateFirst = s.activateFirst;
    for (Shader shader : s.shaders) {
      Shader copy = new Shader(shader);
      this.shaders.add(copy);
      if (s.active.contains(shader)) {
        this.active.add(copy);
      }
    }
  }

  /**
   * Adds a shader. If a shader with the given name already exists, that one is
   * returned instead.
   *
   * <p>
   * The first shader added is drawn with right away; the ones after it wait
   * until {@link #switchTo(String)}, {@link #next()} or
   * {@link #activate(String)} picks them.
   *
   * <p>
   * The vertex shader is usually {@code null}, which uses Processing's default
   * one. In the browser any other vertex shader is ignored.
   *
   * @param name               a unique name
   * @param fragmentShaderPath the path to the fragment shader
   * @param vertexShaderPath   the path to the vertex shader, may be null
   * @return the shader
   *
   * @example.files ShadersAdd.java
   */
  public Shader add(String name, final String fragmentShaderPath, final String vertexShaderPath) {
    Shader existing = this.get(name);
    if (existing != null) {
      return existing;
    }
    return this.add(new Shader(name, fragmentShaderPath, vertexShaderPath));
  }

  /** Adds a shader that is already loaded. */
  Shader add(Shader shader) {
    if (this.shaders.isEmpty() && this.activateFirst) {
      this.active.add(shader);
    }
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
   * Switches to the shader with the given name, or with the given position in
   * the order the shaders were added. It is then the only one drawn with. A
   * name that is not there leaves the shaders as they are and prints which ones
   * there are.
   *
   * @param name the name of the shader
   *
   * @example.files ShadersSwitchTo.java
   */
  public void switchTo(String name) {
    Shader shader = this.find(name);
    if (shader != null) {
      this.active.clear();
      this.active.add(shader);
    }
  }

  /**
   * Switches to the shader at the given position. It is then the only one drawn
   * with.
   *
   * @param index the position of the shader
   */
  public void switchTo(double index) {
    if (this.shaders.isEmpty()) {
      return;
    }
    this.active.clear();
    this.active.add(this.shaders.get(Math.floorMod((int) index, this.shaders.size())));
  }

  /**
   * Switches to the next shader, in the order they were added. After the last
   * one comes the first one again. It is then the only one drawn with.
   *
   * @example.files ShadersNext.java
   */
  public void next() {
    if (this.shaders.isEmpty()) {
      return;
    }
    int next = (this.getCurrentIndex() + 1) % this.shaders.size();
    this.active.clear();
    this.active.add(this.shaders.get(next));
  }

  /**
   * Stops drawing with shaders, so the sprite or stage looks as it does
   * without one. The shaders are kept: {@link #switchTo(String)},
   * {@link #next()} or {@link #activate(String)} brings one back.
   *
   * @example.files ShadersReset.java
   */
  public void reset() {
    this.active.clear();
    this.activateFirst = false;
  }

  /**
   * Draws with the shader of the given name as well, without stopping the
   * others. Active shaders take turns in the order they were added, each
   * working on what the one before it drew, so a stage can be tinted and
   * wobble at the same time. A sprite draws with the first of its active
   * shaders only.
   *
   * @param name the name of the shader
   *
   * @example.files ShadersActivate.java
   */
  public void activate(String name) {
    Shader shader = this.find(name);
    if (shader != null) {
      this.active.add(shader);
    }
  }

  /**
   * Stops drawing with the shader of the given name. The other active shaders
   * stay.
   *
   * @param name the name of the shader
   *
   * @example.files ShadersActivate.java
   */
  public void deactivate(String name) {
    Shader shader = this.find(name);
    if (shader != null) {
      this.active.remove(shader);
    }
  }

  /**
   * Returns whether the shader of the given name is drawn with.
   *
   * @param name the name of the shader
   * @return true if the shader is active
   */
  public boolean isActive(String name) {
    Shader shader = this.get(name);
    return shader != null && this.active.contains(shader);
  }

  /**
   * Returns the shaders drawn with, in the order they take turns: the order in
   * which they were added.
   *
   * @return a new list of the active shaders, empty if there are none
   */
  public List<Shader> getActive() {
    List<Shader> result = new ArrayList<>();
    for (Shader shader : this.shaders) {
      if (this.active.contains(shader)) {
        result.add(shader);
      }
    }
    return result;
  }

  /**
   * Returns the shader currently drawn with. With several active, this is the
   * first of them.
   *
   * @return the current shader, or null if there is none
   */
  public Shader getCurrent() {
    for (Shader shader : this.shaders) {
      if (this.active.contains(shader)) {
        return shader;
      }
    }
    return null;
  }

  /**
   * Returns the position of the current shader.
   *
   * @return the position, or -1 if no shader is in use
   */
  public int getCurrentIndex() {
    Shader current = this.getCurrent();
    return current == null ? -1 : this.shaders.indexOf(current);
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

  /** The shader of that name, or null after saying which ones there are. */
  private Shader find(String name) {
    Shader shader = this.get(name);
    if (shader != null) {
      return shader;
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
      for (Shader each : this.shaders) {
        System.err.println("  - '" + each.getName() + "'");
      }
      System.err.println("\nTip: Check the spelling of your shader name.");
    }
    System.err.println("==============================================\n");
    return null;
  }
}
