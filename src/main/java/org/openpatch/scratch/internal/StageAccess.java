package org.openpatch.scratch.internal;

import java.util.Queue;

import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Stage;

/**
 * How an extension package reaches something on {@link Stage} that is not part
 * of its public face.
 *
 * <p>
 * {@code TiledMap} has to put a map's tiles into the stage's stamp queues, and
 * it lives in its own package. Without this, the queue-taking stamp method would
 * have to be public on {@code Stage}, where it would sit in every learner's
 * autocomplete while taking a type - {@link Stamp} - that they have no way to
 * build. The same trick as {@link StageHooks}, in the other direction: that one
 * lets the render loop into a stage, this one lets an extension in.
 *
 * <p>
 * {@code Stage} installs an implementation when the class is loaded, which has
 * always happened by the time anything can call {@link #get()} - reaching it
 * requires a stage to pass in.
 */
public abstract class StageAccess {

  private static StageAccess instance;

  /**
   * Installs the implementation. Called by {@code Stage}; the first one wins, so
   * this cannot be swapped out from outside the library.
   *
   * @param access the implementation to install
   */
  public static void install(StageAccess access) {
    if (instance == null) {
      instance = access;
    }
  }

  /**
   * Returns the installed accessor.
   *
   * @return the accessor
   * @throws IllegalStateException if no stage has been loaded, which should be
   *                               impossible from anywhere that has a stage to
   *                               hand
   */
  public static StageAccess get() {
    if (instance == null) {
      throw new IllegalStateException(
          "No Stage has been loaded, so the stage accessor was never installed.");
    }
    return instance;
  }

  /**
   * Stamps images permanently onto one of a stage's layers.
   *
   * @param stage  the stage to stamp onto
   * @param stamps the images to stamp
   * @param layer  which layer to stamp them onto
   */
  public abstract void stamp(Stage stage, Queue<Stamp> stamps, Layer layer);
}
