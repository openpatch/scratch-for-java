package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;

/**
 * Switching stages, plain and through a transition, headless. Ports 6.x's WindowTransitionTest
 * and WindowStageLifetimeTest, which needed a window and a child process.
 */
class StageLifetimeTest {

  Game game;
  final List<String> log = new ArrayList<>();

  class Logged extends Stage {
    final String name;

    Logged(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      StageLifetimeTest.this.log.add(this.name);
    }
  }

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
  }

  // ---- transitions (6.x WindowTransitionTest) ----

  @Test
  void theSupplierRunsOnceOnTheNextTickNotWhenTheTransitionIsAsked() {
    Logged from = new Logged("from");
    this.game.setStage(from);
    this.game.runTicks(1);
    int[] made = {0};
    Logged to = new Logged("to");
    this.game.transition(() -> {
      made[0]++;
      return to;
    }, "fade", null, 0.5);
    assertEquals(0, made[0], "not made eagerly");
    this.game.runTicks(1);
    assertEquals(1, made[0]);
    assertSame(to, this.game.getStage());
    assertTrue(this.game.isTransitioning());
  }

  @Test
  void neitherStageRunsDuringTheTransitionAndTheNewOneRunsAfterIt() {
    Logged from = new Logged("from");
    this.game.setStage(from);
    this.game.runTicks(1);
    this.log.clear();
    this.game.transition(() -> new Logged("to"), "fade", null, 0.5);
    this.game.runTicks(30);
    assertEquals(List.of(), this.log, "both stand still for 0.5 s");
    this.game.runTicks(1);
    assertFalse(this.game.isTransitioning());
    assertEquals(List.of("to"), this.log);
  }

  @Test
  void aTransitionAskedDuringATransitionDoesNotMakeItsStage() {
    this.game.setStage(new Logged("from"));
    this.game.runTicks(1);
    Logged to = new Logged("to");
    List<String> made = new ArrayList<>();
    this.game.transition(() -> {
      made.add("to");
      // asked from inside the supplier
      this.game.transition(() -> {
        made.add("reentrant");
        return new Logged("reentrant");
      }, "fade", null, 0.5);
      return to;
    }, "fade", null, 0.5);
    this.game.runTicks(2);
    // asked while busy
    this.game.transition(() -> {
      made.add("busy");
      return new Logged("busy");
    }, "fade", null, 0.5);
    this.game.runTicks(60);
    assertEquals(List.of("to"), made);
    assertSame(to, this.game.getStage());
  }

  // ---- stage lifetime (6.x WindowStageLifetimeTest) ----

  @Test
  void aDiscardedStageIsCollected() throws Exception {
    Stage saved = new Logged("saved");
    this.game.setStage(saved);
    this.game.runTicks(1);
    WeakReference<Stage> discarded = this.showAndLeave(saved);
    long start = System.nanoTime();
    while (discarded.get() != null && System.nanoTime() - start < TimeUnit.SECONDS.toNanos(8)) {
      System.gc();
      Thread.sleep(10);
    }
    assertNull(discarded.get(), "nothing holds on to a stage that is no longer shown");
  }

  /** Shows a stage with a sprite, a pen and a text, then goes back to {@code back}. */
  private WeakReference<Stage> showAndLeave(Stage back) {
    Stage temporary = new Logged("temporary");
    temporary.add(new Sprite());
    temporary.add(new Pen());
    temporary.add(new Text("temporary", 0, 0, 100));
    this.game.setStage(temporary);
    this.game.runTicks(2);
    this.game.transition(() -> back, "fade", null, 0.1);
    this.game.runTicks(10);
    return new WeakReference<>(temporary);
  }

  @Test
  void aKeptStageRunsAgainWhenShownAgain() {
    Logged saved = new Logged("saved");
    Logged other = new Logged("other");
    this.game.setStage(saved);
    this.game.runTicks(1);
    this.game.setStage(other);
    this.game.runTicks(1);
    this.game.setStage(saved);
    this.game.runTicks(1);
    assertEquals(List.of("saved", "other", "saved"), this.log);
  }
}
