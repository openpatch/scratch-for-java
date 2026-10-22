package org.openpatch.scratch.extension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.MouseCode;
import org.openpatch.scratch.Stage;

/**
 * What an extension may hook into. Each method says
 * which thread its hook runs on. An exception thrown by a hook is printed once, naming the
 * extension, and the hook is switched off; the game goes on.
 */
public interface ExtensionContext {

  /** A key went down or up (auto-repeat never arrives). */
  record KeyEvent(KeyCode key, boolean pressed) {}

  /** Something the mouse did, in stage coordinates. */
  record MouseEvent(Kind kind, MouseCode button, double x, double y, double wheel) {
    /** What happened. */
    public enum Kind {
      PRESS,
      RELEASE,
      MOVE,
      WHEEL
    }
  }

  /** An exception from learner code, with the learner's frame (null if there is none). */
  record LearnerError(Throwable error, StackTraceElement learnerFrame) {}

  /** A drawn frame at stage size; its pixels are read back only when asked for. */
  interface Frame {
    int getWidth();

    int getHeight();

    /** Straight ARGB, top row first. */
    int[] getArgb();

    void savePng(Path file) throws IOException;
  }

  /** Decoded pixels from an image format of an extension's. */
  record DecodedImage(int width, int height, int[] argb) {}

  /** Turns the bytes of an image file into pixels; runs on loading threads. */
  @FunctionalInterface
  interface ImageDecoder {
    DecodedImage decode(byte[] file) throws IOException;
  }

  /** Draws into a layer of a stage. */
  @FunctionalInterface
  interface LayerDrawer {
    void draw(Stage stage, Canvas canvas);
  }

  /**
   * A slot in the input snapshot: polled on the window thread, copied to the game thread with
   * each batch of ticks, so no locking is needed.
   *
   * @param <T> the state kept in the slot
   */
  final class InputKey<T> {
    final String name;
    final Supplier<T> factory;
    final BiConsumer<T, T> copy;

    private InputKey(String name, Supplier<T> factory, BiConsumer<T, T> copy) {
      this.name = name;
      this.factory = factory;
      this.copy = copy;
    }

    /**
     * @param name for messages
     * @param factory makes an empty state (twice: one per thread)
     * @param copy copies the window thread's state into the game thread's: (from, into)
     * @param <T> the state
     * @return the key
     */
    public static <T> InputKey<T> of(String name, Supplier<T> factory, BiConsumer<T, T> copy) {
      return new InputKey<>(name, factory, copy);
    }

    /** For the core: makes a state for this key. */
    public T create() {
      return this.factory.get();
    }

    /** For the core: copies one state into another. */
    public void copy(T from, T into) {
      this.copy.accept(from, into);
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  /** The window thread's side of the input snapshot, filled by {@link #onInputPoll}. */
  interface InputSnapshot {
    <T> T get(InputKey<T> key);
  }

  // ---- game thread ---------------------------------------------------------------------

  /** A stage was made (on the thread that made it, usually the game thread or main). */
  void onStageCreated(Consumer<Stage> hook);

  /** A stage stopped being shown. Game thread. */
  void onStageDisposed(Consumer<Stage> hook);

  /** Every tick, after the tick's input and before timers and {@code run()}. Game thread. */
  void beforeTick(Consumer<Stage> hook);

  /** Every key press and release, before learner code sees it; observe only. Game thread. */
  void onKey(Consumer<KeyEvent> hook);

  /** Every mouse event, before learner code sees it; observe only. Game thread. */
  void onMouse(Consumer<MouseEvent> hook);

  /** At the end of the program, after {@code whenExits()}. Game thread. */
  void onExit(Runnable hook);

  /** A learner exception stopped the game. Game thread. */
  void onLearnerError(Consumer<LearnerError> hook);

  /**
   * Draws into a layer every frame, after the layer's canvas and before its sprites: map tiles,
   * drawings. Game thread, when the draw list is made.
   */
  void addLayerDrawer(Layer layer, LayerDrawer drawer);

  /**
   * Draws above everything (UI, texts and monitors included) in stage coordinates; captures leave
   * it out (Studio's watch pins). Game thread.
   */
  void addOverlayDrawer(LayerDrawer drawer);

  /**
   * Calls a listener interface on the stage and every sprite of it that implements it, at the
   * point of the tick where key events arrive. Learner exceptions thrown there stop the game like
   * any learner exception. Game thread.
   *
   * @param <T> the listener interface
   */
  <T> void dispatch(Stage stage, Class<T> listener, Consumer<? super T> call);

  /** This batch's copy of a slot of the input snapshot. Game thread. */
  <T> T input(InputKey<T> key);

  /** @return ticks run so far */
  long getTickCount();

  /** @return whether the loading screen is up */
  boolean isLoading();

  // ---- window thread ---------------------------------------------------------------------

  /**
   * Once per batch of ticks, on the window thread (the one that started GLFW): poll devices into
   * the snapshot. GLFW polling functions only; setting GLFW callbacks is not allowed.
   */
  void onInputPoll(Consumer<InputSnapshot> hook);

  /** After a frame was drawn, on the window thread. */
  void afterFrame(BiConsumer<Stage, Frame> hook);

  // ---- any thread ------------------------------------------------------------------------

  /** Runs a task at the start of the next tick, before input. */
  void runOnGameThread(Runnable task);

  // ---- loading threads ---------------------------------------------------------------------

  /**
   * Images whose name ends in {@code suffix} (e.g. {@code ".ase"}) are decoded by
   * {@code decoder}.
   */
  void registerImageFormat(String suffix, ImageDecoder decoder);
}
