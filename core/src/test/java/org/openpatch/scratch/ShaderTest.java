package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.ShaderDraw;

class ShaderTest {

  Stage stage;

  @BeforeEach
  void setUp() {
    Game game = Game.headless(480, 360);
    this.stage = new Stage();
    game.setStage(this.stage);
  }

  @Test
  void theFirstShaderIsActiveAndItsValuesTravelInTheDrawList() {
    Sprite s = new Sprite("red", SceneTest.RED);
    this.stage.add(s);
    Shader pixelate = s.getShaders().add("pixelate", "shaders/pixelate.frag");
    pixelate.set("blocks", 8.0);
    ShaderDraw drawn = s.toDraw().shaders().get(0);
    assertTrue(drawn.source().contains("floor(uv * blocks)"));
    assertArrayEquals(new float[] {8f}, (float[]) drawn.uniforms().get("blocks"));
    pixelate.set("blocks", 16.0);
    assertArrayEquals(new float[] {8f}, (float[]) drawn.uniforms().get("blocks"), "a copy");
    s.getShaders().reset();
    assertTrue(s.toDraw().shaders().isEmpty());
  }

  @Test
  void headerUniformsCannotBeSet() {
    Shader shader = this.stage.getShaders().add("p", "shaders/pixelate.frag");
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> shader.set("time", 1.0));
    assertTrue(e.getMessage().contains("\"time\" is filled in by Scratch for Java"), e.getMessage());
  }

  @Test
  void aCloneGetsItsOwnShaders() {
    Sprite s = new Sprite("red", SceneTest.RED);
    this.stage.add(s);
    s.getShaders().add("pixelate", "shaders/pixelate.frag").set("blocks", 4.0);
    Sprite clone = s.createClone();
    clone.getShaders().get("pixelate").set("blocks", 2.0);
    assertArrayEquals(new float[] {4f}, (float[]) s.toDraw().shaders().get(0).uniforms().get("blocks"));
    assertEquals(1, clone.toDraw().shaders().size());
  }
}
