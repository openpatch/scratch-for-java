package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.DrawList;
import org.openpatch.scratch.internal.draw.TextDraw;

/** Texts and bubbles, laid out without a window. */
class TextTest {

  Game game;
  Stage stage;

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
    this.stage = new Stage();
    this.game.setStage(this.stage);
  }

  DrawList frame() {
    return this.game.drawList();
  }

  @Test
  void aBubbleHangsOffTheTopRightCornerOfThePaintedPixels() {
    Sprite s = new Sprite("left", SceneTest.LEFT4);
    this.stage.add(s);
    s.say("Hello!");
    TextDraw bubble = this.frame().overlay().get(0);
    // painted columns of left4 are x = -10..-6, rows y = -5..5
    assertEquals(-6, bubble.x(), 1e-9);
    assertEquals(TextDraw.Box.SPEECH, bubble.box());
    assertTrue(bubble.width() >= 80, "a bubble is at least 80 wide");
    assertEquals(5 + bubble.height(), bubble.y(), 1e-9, "the bubble stands on the sprite's top edge");
  }

  @Test
  void sayForSecondsCountsGameTime() {
    Sprite s = new Sprite("red", SceneTest.RED);
    this.stage.add(s);
    s.say("Hi", 2);
    this.game.runTicks(119);
    assertEquals(1, this.frame().overlay().size());
    this.game.runTicks(1);
    assertTrue(this.frame().overlay().isEmpty());
  }

  @Test
  void anEmptyTextRemovesTheBubble() {
    Sprite s = new Sprite("red", SceneTest.RED);
    this.stage.add(s);
    s.think("Hmm");
    assertEquals(TextDraw.Box.THOUGHT, this.frame().overlay().get(0).box());
    s.think("");
    assertTrue(this.frame().overlay().isEmpty());
  }

  @Test
  void longBubblesWrapAt330() {
    Sprite s = new Sprite("red", SceneTest.RED);
    this.stage.add(s);
    s.say("word ".repeat(60));
    TextDraw bubble = this.frame().overlay().get(0);
    assertTrue(bubble.layout().lines().size() > 1);
    assertTrue(bubble.width() <= 330 + 1e-9, "width " + bubble.width());
  }

  @Test
  void uiTextsGoInFrontOfTheUiLayerAndWorldTextsIntoTheOverlay() {
    Text world = new Text("world", 0, 0, 0), ui = new Text("ui", 0, 0, 0);
    ui.setLayer(Layer.UI);
    this.stage.add(world);
    this.stage.add(ui);
    DrawList list = this.frame();
    assertEquals(1, list.overlay().size());
    assertEquals(1, list.ui().size());
    assertEquals("ui", list.ui().get(0).layout().lines().get(0).text());
  }

  @Test
  void plainTextIsCentredOnItsPositionAndBoxesStandOnTheirBottomEdge() {
    Text plain = new Text("Hello", 10, 20, 0);
    Text box = new Text("Hello", 10, 20, 0, TextStyle.BOX);
    Text left = new Text("Hello", 10, 20, 0);
    left.setAlign(TextAlign.LEFT);
    this.stage.add(plain);
    this.stage.add(box);
    this.stage.add(left);
    TextDraw p = this.frame().overlay().get(0), b = this.frame().overlay().get(1), l = this.frame().overlay().get(2);
    // as in 6.x: a label put at a point is centred on it, its first line's letters on its y
    var font = p.layout().font();
    double size = p.layout().size();
    double firstLineMiddle = p.y() - font.ascent(size) + (font.ascent(size) - font.descent(size)) / 2;
    assertEquals(10 - p.width() / 2, p.x(), 1e-9, "plain text is centred on its x");
    assertEquals(20, firstLineMiddle, 1e-9, "and its first line on its y");
    assertEquals(10, l.x(), 1e-9, "left-aligned text starts at its x");
    assertEquals(p.y(), l.y(), 1e-9, "at the same height");
    assertEquals(20 + b.height(), b.y(), 1e-9);
    assertEquals(10 - b.width() / 2, b.x(), 1e-9, "a box is centred on its x");
  }

  @Test
  void aTextOfItsOwnWrapsAtItsWidth() {
    Text t = new Text("one two three four", 0, 0, 60);
    this.stage.add(t);
    for (var line : this.frame().overlay().get(0).layout().lines()) {
      assertTrue(line.width() <= 60, line.text());
    }
    t.hide();
    assertFalse(t.isVisible());
    assertTrue(this.frame().overlay().isEmpty());
  }
}
