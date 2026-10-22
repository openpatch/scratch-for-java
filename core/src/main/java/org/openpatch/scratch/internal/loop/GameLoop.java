package org.openpatch.scratch.internal.loop;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.LearnerError;
import org.openpatch.scratch.internal.draw.DrawList;

/**
 * The game thread and its lockstep with the window thread. The window
 * thread hands over a batch of ticks with {@link #runBatch(int, Runnable)} and waits for the
 * draw list, polling meanwhile, so nothing is drawn while learner code runs and the window stays
 * movable and closable when learner code hangs. A watchdog names the learner's line when a batch
 * takes longer than 2 s.
 */
public final class GameLoop {

  /** How long a batch may take before the watchdog speaks up. */
  static final long HANG_NANOS = 2_000_000_000L;
  /** How long {@code whenExits()} may take before the program ends anyway. */
  static final long EXIT_MILLIS = 1000;

  private static final int EXIT = -1;

  private final Game game;
  private final BlockingQueue<Integer> batches = new LinkedBlockingQueue<>();
  private final BlockingQueue<DrawList[]> results = new LinkedBlockingQueue<>();
  private final CountDownLatch exited = new CountDownLatch(1);
  private final Thread thread;
  private final Thread watchdog;
  private final PrintStream err;
  private volatile long batchStarted;
  private volatile boolean closed;

  public GameLoop(Game game) {
    this(game, System.err);
  }

  GameLoop(Game game, PrintStream err) {
    this.game = game;
    this.err = err;
    this.thread = new Thread(this::loop, "scratch4j-game");
    this.thread.setDaemon(true);
    this.watchdog = new Thread(this::watch, "scratch4j-watchdog");
    this.watchdog.setDaemon(true);
  }

  public void start() {
    this.thread.start();
    this.watchdog.start();
  }

  public Thread getThread() {
    return this.thread;
  }

  /**
   * Runs {@code ticks} on the game thread and returns the draw list made after them. While the
   * game thread works, {@code whileWaiting} runs every few milliseconds (GLFW polling) and may
   * call {@link #close()}; then this returns null.
   */
  public DrawList runBatch(int ticks, Runnable whileWaiting) {
    this.batches.add(ticks);
    try {
      while (true) {
        DrawList[] result = this.results.poll(5, TimeUnit.MILLISECONDS);
        if (result != null) {
          return result[0];
        }
        whileWaiting.run();
        if (this.closed) {
          return null;
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    }
  }

  /** Makes a waiting {@link #runBatch} return; called when the window is closed. */
  public void close() {
    this.closed = true;
  }

  /**
   * Runs {@code whenExits()} on the game thread and waits at most 1 s for it.
   *
   * @return whether the game thread answered in time (a hanging one is skipped)
   */
  public boolean exit() {
    this.batches.add(EXIT);
    try {
      return this.exited.await(EXIT_MILLIS, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  private void loop() {
    try {
      while (true) {
        int ticks = this.batches.take();
        if (ticks == EXIT) {
          this.game.exit();
          this.exited.countDown();
          return;
        }
        this.batchStarted = System.nanoTime();
        DrawList list;
        try {
          if (ticks == 0) {
            this.game.runInputOnly(); // paused, or a screen faster than the ticks: input still arrives
          } else {
            this.game.runTicks(ticks);
          }
          org.openpatch.scratch.internal.Preview.beforeDraw(this.game);
          list = this.game.drawList();
          org.openpatch.scratch.internal.Preview.afterDraw();
        } finally {
          this.batchStarted = 0;
        }
        this.results.add(new DrawList[] {list});
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void watch() {
    long reportedFor = 0;
    while (true) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        return;
      }
      long started = this.batchStarted;
      if (started == 0 || started == reportedFor || System.nanoTime() - started < HANG_NANOS) {
        continue;
      }
      reportedFor = started;
      this.err.print(hangMessage(this.thread.getStackTrace()));
    }
  }

  /** "MySprite.run (MySprite.java:12) has not returned for 2 seconds. …" with the learner's frames. */
  static String hangMessage(StackTraceElement[] trace) {
    List<StackTraceElement> learner = new ArrayList<>();
    for (StackTraceElement e : trace) {
      if (LearnerError.isLearnerFrame(e)) {
        learner.add(e);
      }
    }
    StringBuilder out = new StringBuilder();
    if (learner.isEmpty()) {
      out.append("The game has not finished a tick for 2 seconds.\n");
    } else {
      out.append(LearnerError.describe(learner.get(0)))
          .append(" has not returned for 2 seconds. Is there a loop that never ends?\n");
    }
    for (StackTraceElement e : learner) {
      out.append("    at ").append(e).append('\n');
    }
    out.append("The window stays open; closing it ends the program.\n");
    return out.toString();
  }
}
