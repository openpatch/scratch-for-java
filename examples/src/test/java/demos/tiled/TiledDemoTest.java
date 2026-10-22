package demos.tiled;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.input.InputEvent;

/**
 * The tiled demo against its real map, without a window: the walls and the water block the
 * player, and the warp notices it. (The warp's transition itself needs a window; here only the
 * touching is checked.)
 */
class TiledDemoTest {

  private Game game;
  private Player player;

  @BeforeEach
  void setUp() {
    I18n.setup();
    GameState.load();
    var state = GameState.get();
    // a save on this machine must not move the start around
    state.items = new String[0];
    state.playerX = Double.MAX_VALUE;
    state.playerY = Double.MAX_VALUE;
    state.map = "Level1";
    this.game = Game.headless(640, 360);
    var level = new Level("Level1", null);
    this.game.setStage(level);
    this.player = level.getPlayer();
  }

  @AfterEach
  void tearDown() {
    this.game.setStage(null);
  }

  private void hold(KeyCode key) {
    this.game.post(new InputEvent.Key(key, true));
  }

  @Test
  void theWallsBlockThePlayer() {
    // the wall on the right (Tiled object 18) covers x 902..959 at the player's height;
    // 500 ticks of 2 px would cross the whole map without it
    this.hold(KeyCode.RIGHT);
    this.game.runTicks(500);

    assertTrue(this.player.getX() > 700, "the player moved right, to " + this.player.getX());
    assertTrue(this.player.getX() < 902, "the wall stopped the player at " + this.player.getX());
  }

  @Test
  void theWaterBlocksThePlayerWithoutTheScroll() {
    // the lake (Tiled object 2) covers x 247.6..391.4 at the player's height
    this.hold(KeyCode.LEFT);
    this.game.runTicks(500);

    assertTrue(this.player.getX() < 505, "the player moved left, to " + this.player.getX());
    assertTrue(this.player.getX() > 391.4, "the water stopped the player at " + this.player.getX());
  }

  @Test
  void theWarpNoticesThePlayer() {
    // the warp at the top (Tiled object 25) covers x 482..541, stage y -21..-1
    this.player.setPosition(505, -5);
    this.game.runTicks(1);

    assertNotNull(this.player.getTouchingSprite(Warp.class), "the player touches the warp");
  }
}
