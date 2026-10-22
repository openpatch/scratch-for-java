package org.openpatch.scratch.internal;

import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.MouseCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.internal.draw.DrawList;
import org.openpatch.scratch.internal.input.InputEvent;
import org.openpatch.scratch.internal.input.InputState;
import org.openpatch.scratch.internal.loop.TickClock;

/**
 * The running program, one per process: the stage size, the current stage, the ticks, the input
 * the game thread sees, and what happens when learner code throws. {@code Window} creates it
 * together with a GLFW window; tests create it with {@link #headless(int, int)} and call
 * {@link #runTicks(int)} themselves, which makes the calling thread the game thread. Nothing here
 * touches OpenGL.
 *
 * <p>Everything but {@link #post(InputEvent)}, {@link #runOnGameThread(Runnable)} and the
 * getters for the stage size runs on the game thread.
 */
public final class Game {

  /**
   * What the game needs from the scene classes, which keep their plumbing out of the public
   * API: {@code Stage} registers the implementation when it is loaded (the JDK's "shared
   * secrets" pattern).
   */
  public interface Scene {

    /** Timers, broadcasts, glides, animations, then {@code run()} of the stage and its sprites. */
    void tick(Stage stage);

    /** Hands an input event to the stage and its sprites (whenKeyPressed, whenClicked, …). */
    void dispatch(Stage stage, InputEvent event);

    /** Describes the stage for the renderer. */
    DrawList draw(Stage stage, long tick);

    /** The stage becomes the current one, or stops being it. */
    void shown(Stage stage, boolean shown);

    /** Runs {@code whenExits()}. */
    void exit(Stage stage);

    /**
     * The stage's sprites back to front, as a java.util.List: core code must not call getAll(),
     * whose type differs in the NRW build.
     */
    java.util.List<org.openpatch.scratch.Sprite> sprites(Stage stage);

    /** The stage's id in draw lists. */
    int id(Stage stage);
  }

  /** The stage's sprites back to front, whatever build this is (see {@link Scene#sprites}). */
  public static java.util.List<org.openpatch.scratch.Sprite> spritesOf(Stage stage) {
    return scene().sprites(stage);
  }

  private static volatile Scene scene;
  private static volatile Game current;

  private final int width;
  private final int height;
  private final boolean headless;
  private final InputState input = new InputState();
  private final Queue<InputEvent> events = new ConcurrentLinkedQueue<>();
  private volatile long makingStageSince;
  private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
  private final List<Consumer<LearnerError>> errorListeners = new CopyOnWriteArrayList<>();
  // presses that happened on the current stage: only these get a release
  private final Set<KeyCode> keysPressedHere = EnumSet.noneOf(KeyCode.class);
  private final Set<MouseCode> buttonsPressedHere = EnumSet.noneOf(MouseCode.class);

  private volatile Stage stage;
  private volatile long tick;
  private volatile LearnerError stopped;
  private volatile Thread gameThread;
  private volatile boolean debug;
  private volatile boolean pixelArt;
  private final List<Runnable> exitHooks = new CopyOnWriteArrayList<>();
  // read by the window's tick clock every frame
  private volatile boolean paused;
  private volatile double gameSpeed = 1;
  private final java.util.concurrent.atomic.AtomicInteger steps = new java.util.concurrent.atomic.AtomicInteger();
  // a transition in progress (game thread only)
  private Stage transitionFrom;
  private DrawList transitionFromList;
  private String transitionName, transitionSource;
  private int transitionTicks, transitionTick;
  /** From the request until the transition is over: further requests are ignored (6.x). */
  private final AtomicBoolean transitionAsked = new AtomicBoolean();

  private final ExtensionHost extensions = new ExtensionHost(this);

  private Game(int width, int height, boolean headless) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("The stage needs a width and height above 0, not " + width + " x "
          + height + ".");
    }
    this.width = width;
    this.height = height;
    this.headless = headless;
  }

  /** Called by {@code Window}: the one game of this process. */
  public static synchronized Game start(int width, int height) {
    if (current != null) {
      throw new IllegalStateException("There can only be one window. Use setStage to show another stage.");
    }
    current = new Game(width, height, false);
    return current;
  }

  /**
   * A game without a window, for tests: replaces any previous one. The thread that calls
   * {@link #runTicks(int)} is its game thread.
   */
  public static synchronized Game headless(int width, int height) {
    current = new Game(width, height, true);
    return current;
  }

  /** The running game; a stage or sprite created before the window gets this message. */
  public static Game current() {
    Game game = current;
    if (game == null) {
      throw new IllegalStateException(
          "There is no window yet. Create it first, e.g. new Window(480, 360), then the stage.");
    }
    return game;
  }

  /** The running game, or null before the window exists. */
  public static Game currentOrNull() {
    return current;
  }

  /** Called once by {@code Stage}'s static initializer. */
  public static void register(Scene implementation) {
    scene = implementation;
  }

  private static Scene scene() {
    if (scene == null) {
      // Stage registers itself when its class is initialised
      try {
        Class.forName(Stage.class.getName(), true, Stage.class.getClassLoader());
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }
    }
    return scene;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public boolean isHeadless() {
    return this.headless;
  }

  /** The installed extensions' hooks. */
  public ExtensionHost getExtensions() {
    return this.extensions;
  }

  public boolean isDebug() {
    return this.debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /** Pixel-art display mode: set by Window before the first stage. */
  public boolean isPixelArt() {
    return this.pixelArt;
  }

  public void setPixelArt(boolean pixelArt) {
    this.pixelArt = pixelArt;
  }

  // ---- pause and speed (any thread; the window's tick clock reads them) -----------

  public boolean isPaused() {
    return this.paused;
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  /** While paused: one more tick. */
  public void step() {
    this.steps.incrementAndGet();
  }

  /** Steps asked for since the last call, for the tick clock. */
  public int takeSteps() {
    return this.steps.getAndSet(0);
  }

  public double getGameSpeed() {
    return this.gameSpeed;
  }

  public void setGameSpeed(double speed) {
    if (!(speed >= 0 && speed <= TickClock.MAX_TICKS_PER_FRAME)) {
      throw new IllegalArgumentException("The game speed has to be between 0 and 8, not " + speed + ".");
    }
    this.gameSpeed = speed;
  }

  // ---- stages ---------------------------------------------------------------

  public Stage getStage() {
    return this.stage;
  }

  /**
   * Shows {@code stage} from the next tick on. Presses on the old stage get no release on the new
   * one.
   */
  public void setStage(Stage stage) {
    Stage old = this.stage;
    if (old == stage) {
      return;
    }
    this.stage = stage;
    this.keysPressedHere.clear();
    this.buttonsPressedHere.clear();
    if (old != null) {
      scene().shown(old, false);
      this.extensions.stageDisposed(old);
    }
    if (stage != null) {
      scene().shown(stage, true);
    }
  }

  /**
   * Shows the stage {@code next} makes, through a transition: {@code next} runs on the game thread
   * at the start of the next tick; the old stage stands still meanwhile, input is dropped, and the
   * new stage's {@code run()} starts once the transition is over. Requests made meanwhile (also
   * from inside {@code next}) are ignored, as in 6.x: two sprites touching a door in the same tick
   * build one level, not two. Any thread.
   *
   * @param name a built-in transition's name, or a shader file
   * @param source a transition shader's body, or null for a built-in one
   */
  public void transition(java.util.function.Supplier<? extends Stage> next, String name, String source,
      double seconds) {
    if (!this.transitionAsked.compareAndSet(false, true)) {
      return;
    }
    this.runOnGameThread(() -> {
      Stage from = this.stage;
      // the old stage's picture before the supplier runs: it may move sprites over to the new one
      DrawList fromList = from == null ? null : scene().draw(from, this.tick);
      Stage to;
      this.makingStageSince = System.nanoTime();
      try {
        to = next.get();
      } catch (Throwable t) {
        this.transitionAsked.set(false);
        throw t;
      } finally {
        this.makingStageSince = 0;
      }
      int ticks = (int) Math.round(seconds * 60);
      if (from == null || ticks <= 0) {
        this.setStage(to);
        this.transitionAsked.set(false);
        return;
      }
      this.transitionFromList = fromList;
      this.transitionFrom = from;
      this.transitionName = name;
      this.transitionSource = source;
      this.transitionTicks = ticks;
      this.transitionTick = 0;
      this.setStage(to);
    });
  }

  /**
   * Since when ({@code System.nanoTime()}) a transition's stage supplier has been running, or 0:
   * the window shows the loading screen when it takes long. Any thread.
   */
  public long makingStageSince() {
    return this.makingStageSince;
  }

  /** Drops input that arrived while nothing could take it (the loading screen). Any thread. */
  public void dropInput() {
    this.events.clear();
  }

  /**
   * Drops input during a transition: no handler sees it, but what is held follows it, so a key
   * released meanwhile is not stuck down afterwards. Game thread.
   */
  private void dropInputDuringTransition() {
    for (InputEvent event; (event = this.events.poll()) != null; ) {
      if (event instanceof InputEvent.Key k && !k.pressed()) {
        this.keysPressedHere.remove(k.key());
      } else if (event instanceof InputEvent.Button b && !b.pressed()) {
        this.buttonsPressedHere.remove(b.button());
      }
      this.input.apply(event);
    }
  }

  /** Whether a transition is in progress (game thread). */
  public boolean isTransitioning() {
    return this.transitionFrom != null;
  }

  // ---- ticks ------------------------------------------------------------------

  /** Ticks run so far. */
  public long getTickCount() {
    return this.tick;
  }

  /** Game time in seconds: ticks × 1/60. Stops while paused, follows the game speed. */
  public double getTime() {
    return this.tick * TickClock.TICK;
  }

  /** The thread running the ticks, or null before the first tick. */
  public Thread getGameThread() {
    return this.gameThread;
  }

  /** Whether the current thread is the game thread (true before the first tick, for set-up code). */
  public boolean isGameThread() {
    Thread t = this.gameThread;
    return t == null || t == Thread.currentThread();
  }

  /**
   * Runs a batch of ticks on the calling thread, which becomes the game thread. The batch's first
   * tick gets the input queued since the last batch. After a learner exception nothing runs any
   * more.
   */
  public void runTicks(int ticks) {
    this.gameThread = Thread.currentThread();
    for (int i = 0; i < ticks && this.stopped == null; i++) {
      this.tick(i == 0);
    }
  }

  private void tick(boolean first) {
    try {
      for (Runnable task; (task = this.tasks.poll()) != null; ) {
        task.run();
      }
      Stage stage = this.stage;
      if (this.transitionFrom != null) {
        // both stages stand still and input is dropped
        this.dropInputDuringTransition();
        if (++this.transitionTick >= this.transitionTicks) {
          this.transitionFrom = null;
          this.transitionFromList = null;
          this.transitionAsked.set(false);
        }
        return;
      }
      if (first) {
        this.deliverInput(stage);
      }
      if (stage != null) {
        this.extensions.beforeTick(stage);
        scene().tick(stage);
      }
    } catch (Throwable t) {
      this.failLearner(t);
    } finally {
      this.tick++;
    }
  }

  /**
   * Hands out queued input without running a tick: while paused, events still reach learner
   * code (6.x documents it for keys). Called by the window instead of a batch of 0 ticks.
   */
  public void runInputOnly() {
    this.gameThread = Thread.currentThread();
    if (this.stopped != null) {
      return;
    }
    try {
      for (Runnable task; (task = this.tasks.poll()) != null; ) {
        task.run();
      }
      if (this.transitionFrom == null) {
        this.deliverInput(this.stage);
      }
    } catch (Throwable t) {
      this.stop(LearnerError.of(t));
    }
  }

  private void deliverInput(Stage stage) {
    for (InputEvent event; (event = this.events.poll()) != null; ) {
      this.input(stage, event);
    }
  }

  private void input(Stage stage, InputEvent event) {
    boolean deliver = true;
    if (event instanceof InputEvent.Key k) {
      if (k.pressed()) {
        this.keysPressedHere.add(k.key());
      } else {
        deliver = this.keysPressedHere.remove(k.key());
      }
    } else if (event instanceof InputEvent.Button b) {
      if (b.pressed()) {
        this.buttonsPressedHere.add(b.button());
      } else {
        deliver = this.buttonsPressedHere.remove(b.button());
      }
    }
    this.input.apply(event);
    if (event instanceof InputEvent.Key k) {
      this.extensions.key(new org.openpatch.scratch.extension.ExtensionContext.KeyEvent(k.key(), k.pressed()));
    } else if (event instanceof InputEvent.Button b) {
      this.extensions.mouse(new org.openpatch.scratch.extension.ExtensionContext.MouseEvent(b.pressed()
          ? org.openpatch.scratch.extension.ExtensionContext.MouseEvent.Kind.PRESS
          : org.openpatch.scratch.extension.ExtensionContext.MouseEvent.Kind.RELEASE, b.button(), b.x(), b.y(), 0));
    } else if (event instanceof InputEvent.Move m) {
      this.extensions.mouse(new org.openpatch.scratch.extension.ExtensionContext.MouseEvent(
          org.openpatch.scratch.extension.ExtensionContext.MouseEvent.Kind.MOVE, null, m.x(), m.y(), 0));
    } else if (event instanceof InputEvent.Wheel w) {
      this.extensions.mouse(new org.openpatch.scratch.extension.ExtensionContext.MouseEvent(
          org.openpatch.scratch.extension.ExtensionContext.MouseEvent.Kind.WHEEL, null, this.input.getMouseX(),
          this.input.getMouseY(), w.steps()));
    }
    if (deliver && stage != null) {
      scene().dispatch(stage, event);
    }
  }

  /** Describes the current stage for the renderer; called after a batch, on the game thread. */
  public DrawList drawList() {
    Stage stage = this.stage;
    if (stage == null) {
      return null;
    }
    DrawList list = scene().draw(stage, this.tick);
    if (this.transitionFrom != null) {
      list = list.withTransition(new org.openpatch.scratch.internal.draw.TransitionDraw(this.transitionFromList,
          this.transitionName, this.transitionSource, this.transitionTick / (double) this.transitionTicks));
    }
    return list;
  }

  /** The id the current stage's draw lists carry, or 0 without a stage. */
  public int drawListStageId() {
    Stage stage = this.stage;
    return stage == null ? 0 : scene().id(stage);
  }

  /** The keyboard and mouse as the game thread sees them in this tick. */
  public InputState getInput() {
    return this.input;
  }

  /** Queues an event for the start of the next batch; any thread. */
  public void post(InputEvent event) {
    this.events.add(event);
  }

  /** Runs {@code task} at the start of the next tick, before input; any thread. */
  public void runOnGameThread(Runnable task) {
    this.tasks.add(task);
  }

  // ---- learner errors and the end ------------------------------------------------

  /** Called with every learner exception that stops the game (Studio's IDE control). */
  public void onLearnerError(Consumer<LearnerError> listener) {
    this.errorListeners.add(listener);
  }

  /** The exception that stopped the game, or null while it runs. */
  public LearnerError getStopped() {
    return this.stopped;
  }

  /** Learner code threw (directly, or through an extension's dispatch): the game stops. */
  public void failLearner(Throwable t) {
    if (t instanceof ExtensionHost.LearnerCallFailed && t.getCause() != null) {
      t = t.getCause();
    }
    if (this.stopped == null) {
      this.stop(LearnerError.of(t));
    }
  }

  private void stop(LearnerError error) {
    this.stopped = error;
    System.err.print(error.message());
    if (Boolean.getBoolean("scratch4j.fullTraces") || error.learnerFrame() == null) {
      error.error().printStackTrace();
    }
    for (Consumer<LearnerError> listener : this.errorListeners) {
      try {
        listener.accept(error);
      } catch (RuntimeException e) {
        e.printStackTrace();
      }
    }
  }

  /** Runs at the end, on the game thread: {@code Window.whenExits()}, extensions' onExit. */
  public void onExit(Runnable hook) {
    this.exitHooks.add(hook);
  }

  /** The end of the program, on the game thread: the stage's and the exit hooks. */
  public void exit() {
    Stage stage = this.stage;
    if (stage != null) {
      try {
        scene().exit(stage);
      } catch (Throwable t) {
        System.err.print(LearnerError.of(t).message());
      }
    }
    for (Runnable hook : this.exitHooks) {
      try {
        hook.run();
      } catch (Throwable t) {
        System.err.print(LearnerError.of(t).message());
      }
    }
  }
}
