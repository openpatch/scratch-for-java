package portable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;

/**
 * Runs the checks the online IDE runs unchanged (compatibility/checks, copied here by
 * scripts/release-catalogs.py). Sprites need a game in 7.0: a headless one, as the browser
 * runner has its own.
 */
class PortableBehaviorChecksTest {

  Game game;

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
    this.game.setStage(new org.openpatch.scratch.Stage());
  }

  @AfterEach
  void tearDown() {
    this.game.setStage(null);
  }

  @Test
  void movementCollisionScoreCloneAndRandomness() {
    PortableBehaviorChecks.run();
  }
}
