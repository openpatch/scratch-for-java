package org.openpatch.scratch;

import java.nio.charset.StandardCharsets;
import org.openpatch.scratch.internal.image.Images;

/**
 * How one stage gives way to the next in {@code window.transitionToStage(…)}. A transition is a
 * small shader that mixes the old stage's picture ({@code from}) and the new one's ({@code to})
 * by {@code progress}, from 0 to 1; the built-in ones cover the usual cases, and
 * {@link #fromShader(String)} makes one of your own:
 *
 * <pre>{@code
 * void main() {
 *   color = mix(texture(from, uv), texture(to, uv), progress);
 * }
 * }</pre>
 *
 * <p>During a transition the old stage stands still, the new stage's {@code run()} starts once the
 * transition is over, and input is ignored.
 */
public final class Transition {

  /** Fades the old stage to black, then the new one in. */
  public static final Transition FADE = new Transition("fade", null);
  /** Blends the old stage into the new one. */
  public static final Transition CROSSFADE = new Transition("crossfade", null);
  /** The new stage pushes the old one out to the left. */
  public static final Transition SLIDE_LEFT = new Transition("slide-left", null);
  /** The new stage pushes the old one out to the right. */
  public static final Transition SLIDE_RIGHT = new Transition("slide-right", null);
  /** The new stage pushes the old one out upwards. */
  public static final Transition SLIDE_UP = new Transition("slide-up", null);
  /** The new stage pushes the old one out downwards. */
  public static final Transition SLIDE_DOWN = new Transition("slide-down", null);

  private final String name;
  private final String source;

  private Transition(String name, String source) {
    this.name = name;
    this.source = source;
  }

  /**
   * A transition of your own: a shader body with {@code from}, {@code to} (the two pictures),
   * {@code progress} (0 to 1), {@code uv} and {@code color}.
   *
   * @param path the file with the body
   * @return the transition
   */
  public static Transition fromShader(String path) {
    byte[] bytes = Images.read(path);
    if (bytes == null) {
      throw new IllegalArgumentException("Transition shader not found: " + path);
    }
    return new Transition(path, new String(bytes, StandardCharsets.UTF_8));
  }

  /** @return the built-in name or the shader file */
  public String getName() {
    return this.name;
  }

  /** The shader body of a transition of one's own; null for the built-in ones. */
  String getSource() {
    return this.source;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
