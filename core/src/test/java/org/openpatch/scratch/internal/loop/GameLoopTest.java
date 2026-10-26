package org.openpatch.scratch.internal.loop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.DrawList;

/** The threads of the game loop, without a window: the window thread is the test thread here. */
class GameLoopTest {

  /** Learner code whose run() never returns once told to. */
  static class Hanger extends Sprite {
    static volatile boolean hang;
    volatile long spins;

    @Override
    public void run() {
      while (hang) {
        this.spins++; // a loop that never ends
      }
    }
  }

  @Test
  void aBatchRunsOnTheGameThreadAndHandsBackTheDrawList() {
    Game game = Game.headless(480, 360);
    AtomicReference<String> thread = new AtomicReference<>();
    Stage stage = new Stage() {
      @Override
      public void run() {
        thread.set(Thread.currentThread().getName());
      }
    };
    game.setStage(stage);
    GameLoop loop = new GameLoop(game);
    loop.start();
    DrawList list = loop.runBatch(3, () -> {});
    assertNotNull(list);
    assertEquals(3, list.tick());
    assertEquals("scratch4j-game", thread.get());
  }

  @Test
  void aHangingRunIsNamedAndTheWindowThreadKeepsPollingAndCanClose() throws Exception {
    Game game = Game.headless(480, 360);
    Stage stage = new Stage();
    stage.add(new Hanger());
    game.setStage(stage);
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    GameLoop loop = new GameLoop(game, new PrintStream(err, true, StandardCharsets.UTF_8));
    loop.start();
    Hanger.hang = true;
    try {
      long start = System.nanoTime();
      int[] polls = {0};
      DrawList list = loop.runBatch(1, () -> {
        polls[0]++;
        String said = err.toString(StandardCharsets.UTF_8);
        if (said.contains("has not returned") || System.nanoTime() - start > 5_000_000_000L) {
          loop.close();
        }
      });
      assertNull(list, "closed while learner code hung");
      assertTrue(polls[0] > 100, "the window thread kept polling: " + polls[0]);
      String said = err.toString(StandardCharsets.UTF_8);
      assertTrue(said.startsWith("GameLoopTest$Hanger.run (GameLoopTest.java:"), said);
      assertTrue(said.contains("Is there a loop that never ends?"), said);
      long exitStart = System.nanoTime();
      assertFalse(loop.exit(), "a hanging game thread does not run whenExits");
      long waited = (System.nanoTime() - exitStart) / 1_000_000;
      assertTrue(waited >= 900 && waited < 2000, "waited " + waited + " ms");
    } finally {
      Hanger.hang = false;
    }
  }

  @Test
  void anIdleGameThreadAnswersTheExitAtOnce() {
    Game game = Game.headless(480, 360);
    game.setStage(new Stage());
    GameLoop loop = new GameLoop(game);
    loop.start();
    loop.runBatch(1, () -> {});
    long start = System.nanoTime();
    assertTrue(loop.exit());
    assertTrue(System.nanoTime() - start < 500_000_000L);
  }
}
