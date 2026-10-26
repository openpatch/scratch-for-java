package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.DebugOption;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.DrawList;
import org.openpatch.scratch.internal.draw.Item;
import org.openpatch.scratch.internal.draw.ShapeDraw;
import org.openpatch.scratch.internal.draw.TextDraw;
import org.openpatch.scratch.internal.input.InputEvent;
import org.openpatch.scratch.internal.text.Font;

/** The debug overlay without a window: its options, the panel that switches them, its clicks. */
class DebugOverlayTest {

  static final String RED = "costumes/red10.png";
  static final int HITBOX_COLOR = 0xffff0000;
  static final int HIDDEN_COLOR = 0xff00c8ff;

  Game game;
  Stage stage;
  final List<String> log = new ArrayList<>();

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
    this.stage = new Stage() {
      @Override
      public void whenMouseClicked(MouseCode button) {
        DebugOverlayTest.this.log.add("clicked " + button);
      }
    };
    this.game.setStage(this.stage);
  }

  @Test
  void theOptionsComeFromTheProperty() {
    assertTrue(DebugOption.parse(null).containsAll(EnumSet.of(DebugOption.HITBOXES, DebugOption.LABELS,
        DebugOption.MOUSE, DebugOption.FPS)));
    assertTrue(DebugOption.parse("").isEmpty());
    assertEquals(EnumSet.allOf(DebugOption.class), DebugOption.parse("all"));
    assertEquals(EnumSet.of(DebugOption.HITBOXES, DebugOption.CLONES), DebugOption.parse(" hitboxes ,clones "));
  }

  @Test
  void debugModeShowsTheFamiliarOverlay() {
    this.stage.add(new Sprite("red", RED));
    this.game.setDebug(true);
    this.game.runTicks(1);
    DrawList list = this.game.drawList();
    assertTrue(list.debug()); // the frame rate
    assertTrue(this.strokes(list).contains(HITBOX_COLOR));
    assertTrue(this.texts(list).contains("Direction:"));
    assertTrue(this.texts(list).contains("Sprite (0.0, 0.0)"));
    assertFalse(list.overlayItems().isEmpty()); // the panel's chip
  }

  @Test
  void switchedOffOptionsAreNotShown() {
    this.stage.add(new Sprite("red", RED));
    this.game.setDebug(true);
    this.game.getDebugOptions().clear();
    this.game.runTicks(1);
    DrawList list = this.game.drawList();
    assertFalse(list.debug());
    assertFalse(this.strokes(list).contains(HITBOX_COLOR));
    assertFalse(this.strokes(list).contains(HIDDEN_COLOR));
    assertFalse(this.texts(list).contains("Direction:"));
    assertFalse(this.texts(list).contains("Camera:"));
  }

  @Test
  void touchingPairsGetALineBetweenTheirCentres() {
    Sprite a = new Sprite("red", RED), b = new Sprite("red", RED);
    b.setX(5);
    this.stage.add(a);
    this.stage.add(b);
    this.game.setDebug(true);
    this.game.getDebugOptions().clear();
    this.game.getDebugOptions().add(DebugOption.TOUCHING);
    this.game.runTicks(1);
    assertTrue(a.isTouchingSprite(b));
    DrawList list = this.game.drawList();
    assertTrue(this.shapes(list).stream().anyMatch(s -> s.points().length == 4 && s.points()[0] == a.getX()
        && s.points()[1] == a.getY() && s.points()[2] == b.getX() && s.points()[3] == b.getY()));
  }

  @Test
  void hiddenSpritesGetAnOutline() {
    Sprite hidden = new Sprite("red", RED);
    hidden.hide();
    this.stage.add(hidden);
    this.game.setDebug(true);
    this.game.getDebugOptions().clear();
    this.game.getDebugOptions().add(DebugOption.INVISIBLE);
    this.game.runTicks(1);
    assertTrue(this.strokes(this.game.drawList()).contains(HIDDEN_COLOR));
  }

  @Test
  void theCloneCountIsShown() {
    Sprite s = new Sprite("red", RED);
    this.stage.add(s);
    s.createClone();
    this.game.setDebug(true);
    this.game.getDebugOptions().clear();
    this.game.getDebugOptions().add(DebugOption.CLONES);
    this.game.runTicks(1);
    assertTrue(this.texts(this.game.drawList()).contains("Clones: 1"));
  }

  @Test
  void thePanelTogglesOptionsAndSwallowsItsClicks() {
    this.stage.add(new Sprite("red", RED));
    this.game.setDebug(true);
    this.game.runTicks(1);
    // the chip opens the panel; that click never reaches the learner
    TextDraw chip = (TextDraw) this.game.drawList().overlayItems().get(0);
    this.press(chip.x() + chip.width() / 2, chip.y() - chip.height() / 2);
    assertTrue(this.log.isEmpty());
    DrawList open = this.game.drawList();
    assertTrue(open.overlayItems().size() > 10); // the open panel: box, title, seven rows
    // the first checkbox belongs to the hitboxes, which are on
    ShapeDraw box = (ShapeDraw) open.overlayItems().stream()
        .filter(i -> i instanceof ShapeDraw s && s.fill() == 0x66ff4444).findFirst().orElseThrow();
    this.press((box.points()[0] + box.points()[2]) / 2, (box.points()[1] + box.points()[5]) / 2);
    assertFalse(this.game.getDebugOptions().contains(DebugOption.HITBOXES));
    assertTrue(this.log.isEmpty());
    // ticking it again switches them back on
    this.press((box.points()[0] + box.points()[2]) / 2, (box.points()[1] + box.points()[5]) / 2);
    assertTrue(this.game.getDebugOptions().contains(DebugOption.HITBOXES));
    // a click next to the panel reaches the learner
    this.press(0, 0);
    assertEquals(List.of("clicked LEFT"), this.log);
  }

  @Test
  void clickingALabelSwitchesItsOwnOption() {
    this.game.setDebug(true);
    this.game.runTicks(1);
    TextDraw chip = (TextDraw) this.game.drawList().overlayItems().get(0);
    this.press(chip.x() + chip.width() / 2, chip.y() - chip.height() / 2);
    DrawList open = this.game.drawList();
    // the eye clicks the middle of the letters, which hang down from the box's top edge
    Font font = Font.defaultFont();
    double inkHeight = font.ascent(12) + font.descent(12);
    TextDraw label = this.label(open, "Mouse & camera");
    Set<DebugOption> before = EnumSet.copyOf(this.game.getDebugOptions());
    this.press(label.x() + label.width() / 2, label.y() - inkHeight / 2);
    Set<DebugOption> after = this.game.getDebugOptions();
    Set<DebugOption> expected = EnumSet.copyOf(before);
    expected.remove(DebugOption.MOUSE);
    assertEquals(expected, after);
  }

  private TextDraw label(DrawList list, String text) {
    for (Item item : list.overlayItems()) {
      if (item instanceof TextDraw t && this.text(t).strip().equals(text)) {
        return t;
      }
    }
    throw new AssertionError("no label '" + text + "' in the panel");
  }

  private void press(double x, double y) {
    this.game.post(new InputEvent.Button(MouseCode.LEFT, true, x, y));
    this.game.runTicks(1);
  }

  private List<ShapeDraw> shapes(DrawList list) {
    List<ShapeDraw> out = new ArrayList<>();
    for (Item item : list.overlayItems()) {
      if (item instanceof ShapeDraw s) {
        out.add(s);
      }
    }
    for (var layer : list.layers()) {
      for (Item item : layer.items()) {
        if (item instanceof ShapeDraw s) {
          out.add(s);
        }
      }
    }
    return out;
  }

  private List<Integer> strokes(DrawList list) {
    return this.shapes(list).stream().map(ShapeDraw::stroke).toList();
  }

  private String texts(DrawList list) {
    StringBuilder out = new StringBuilder();
    for (TextDraw t : list.overlay()) {
      out.append(this.text(t));
    }
    for (TextDraw t : list.ui()) {
      out.append(this.text(t));
    }
    for (Item item : list.overlayItems()) {
      if (item instanceof TextDraw t) {
        out.append(this.text(t));
      }
    }
    return out.toString();
  }

  private String text(TextDraw t) {
    StringBuilder out = new StringBuilder();
    for (var line : t.layout().lines()) {
      out.append(line.text()).append('\n');
    }
    return out.toString();
  }
}
