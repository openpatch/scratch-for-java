package org.openpatch.scratch;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.openpatch.scratch.internal.draw.ShaderDraw;

/**
 * The shaders of a sprite or of the stage. The first one added is used at once; more than one
 * can be active, applied in the order they were added.
 *
 * <pre>{@code
 * Shader waves = this.getShaders().add("waves", "assets/waves.frag");
 * waves.set("strength", 0.02);
 * }</pre>
 */
public final class Shaders {

  private final List<Shader> shaders = new ArrayList<>();
  private final Set<Shader> active = new LinkedHashSet<>();
  private boolean activateFirst = true;

  Shaders() {}

  Shaders(Shaders other) {
    this.activateFirst = other.activateFirst;
    for (Shader s : other.shaders) {
      Shader copy = new Shader(s);
      this.shaders.add(copy);
      if (other.active.contains(s)) {
        this.active.add(copy);
      }
    }
  }

  /**
   * Adds a shader, or returns the one of that name added before.
   *
   * @param name the shader's name
   * @param fragmentPath the file with the shader's body
   * @return the shader, to set its uniforms
   *
   * @example.files ShadersAdd.java
   */
  public Shader add(String name, String fragmentPath) {
    Shader existing = this.get(name);
    if (existing != null) {
      return existing;
    }
    Shader shader = new Shader(name, fragmentPath);
    if (this.shaders.isEmpty() && this.activateFirst) {
      this.active.add(shader);
    }
    this.shaders.add(shader);
    return shader;
  }

  /**
   * @param name a shader's name
   * @return the shader, or null
   */
  public Shader get(String name) {
    for (Shader s : this.shaders) {
      if (s.getName().equals(name)) {
        return s;
      }
    }
    return null;
  }

  /**
   * @param name the shader to use, alone
   *
   * @example.files ShadersSwitchTo.java
   */
  public void switchTo(String name) {
    Shader s = this.get(name);
    if (s != null) {
      this.active.clear();
      this.active.add(s);
    }
  }

  /**
   * @param index the shader to use alone, counting from 0; wraps around
   *
   * @example.files ShadersSwitchTo.java
   */
  public void switchTo(double index) {
    if (!this.shaders.isEmpty()) {
      this.active.clear();
      this.active.add(this.shaders.get(Math.floorMod((int) Math.floor(index), this.shaders.size())));
    }
  }

  /**
   * Uses the next shader alone.
   *
   * @example.files ShadersNext.java
   */
  public void next() {
    this.switchTo(this.getCurrentIndex() + 1);
  }

  /**
   * Uses no shader.
   *
   * @example.files ShadersReset.java
   */
  public void reset() {
    this.active.clear();
    this.activateFirst = false;
  }

  /**
   * @param name a shader to use in addition to the active ones
   *
   * @example.files ShadersActivate.java
   */
  public void activate(String name) {
    Shader s = this.get(name);
    if (s != null) {
      this.active.add(s);
    }
  }

  /**
   * @param name a shader to stop using
   *
   * @example.files ShadersActivate.java
   */
  public void deactivate(String name) {
    Shader s = this.get(name);
    if (s != null) {
      this.active.remove(s);
    }
  }

  /**
   * @param name a shader's name
   * @return whether it is in use
   */
  public boolean isActive(String name) {
    Shader s = this.get(name);
    return s != null && this.active.contains(s);
  }

  /** @return the shaders in use, in order */
  public List<Shader> getActive() {
    List<Shader> result = new ArrayList<>();
    for (Shader s : this.shaders) {
      if (this.active.contains(s)) {
        result.add(s);
      }
    }
    return result;
  }

  /** @return the first shader in use, or null */
  public Shader getCurrent() {
    List<Shader> active = this.getActive();
    return active.isEmpty() ? null : active.get(0);
  }

  /** @return the number of the first shader in use, or -1 */
  public int getCurrentIndex() {
    Shader current = this.getCurrent();
    return current == null ? -1 : this.shaders.indexOf(current);
  }

  /** @return the name of the first shader in use, or null */
  public String getCurrentName() {
    Shader current = this.getCurrent();
    return current == null ? null : current.getName();
  }

  List<ShaderDraw> toDraw() {
    List<ShaderDraw> out = new ArrayList<>();
    for (Shader s : this.getActive()) {
      out.add(s.toDraw());
    }
    return List.copyOf(out);
  }
}
