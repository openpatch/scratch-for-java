package org.openpatch.scratch.drawing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.Color;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.Item;
import org.openpatch.scratch.internal.draw.ShapeDraw;

/** The drawing extension on the public API, without a window. */
class DrawingTest {

  Game game;
  Stage stage;

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
    this.game.getExtensions().install(List.of(new DrawingExtension()));
    this.stage = new Stage();
    this.game.setStage(this.stage);
  }

  static class Box extends DrawingSprite {
    @Override
    public void draw(Drawing drawing) {
      drawing.rectangle(0, 0, 20, 10, new Color(255, 0, 0));
    }
  }

  List<Item> main() {
    return this.game.drawList().layers().get(Layer.MAIN.ordinal()).items();
  }

  @Test
  void aDrawingSpriteDrawsWhereItIs() {
    Box box = new Box();
    box.setPosition(100, 50);
    this.stage.add(box);
    ShapeDraw shape = (ShapeDraw) this.main().get(0);
    double[] p = shape.points();
    // corners of a 20 × 10 rectangle around (100, 50)
    assertEquals(90, Math.min(Math.min(p[0], p[2]), Math.min(p[4], p[6])), 1e-9);
    assertEquals(55, Math.max(Math.max(p[1], p[3]), Math.max(p[5], p[7])), 1e-9);
  }

  @Test
  void itTurnsAndHidesWithTheSprite() {
    Box box = new Box();
    this.stage.add(box);
    box.setDirection(180); // a quarter turn clockwise: 20 wide becomes 20 high
    double[] p = ((ShapeDraw) this.main().get(0)).points();
    double maxY = Math.max(Math.max(p[1], p[3]), Math.max(p[5], p[7]));
    assertEquals(10, maxY, 1e-9);
    box.hide();
    assertTrue(this.main().isEmpty());
  }

  @Test
  void aBitmapTextDrawsOneImagePerGlyph() {
    BitmapText text = new BitmapText("costumes/walk%d.png");
    text.addGlyph('A', "costumes/walk1.png");
    text.addGlyph('B', "costumes/walk2.png");
    text.showText("AB A");
    this.stage.add(text);
    assertEquals(3, this.main().size());
    assertTrue(text.getWidth() > 0);
  }
}
