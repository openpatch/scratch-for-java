package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.AskDraw;
import org.openpatch.scratch.internal.input.InputEvent;

/** Timers in game time; ask and display. */
class TimerAndAskTest {

  Game game;
  Stage stage;

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
    this.stage = new Stage();
    this.game.setStage(this.stage);
  }

  @Test
  void aTimerCountsGameTime() {
    Timer t = new Timer();
    this.game.runTicks(90);
    assertEquals(1.5, t.getSeconds(), 1e-9);
    t.reset();
    assertEquals(0, t.getSeconds(), 1e-9);
  }

  @Test
  void everyIsTrueOncePerPeriod() {
    Timer t = new Timer();
    List<Long> ticks = new ArrayList<>();
    for (int i = 0; i < 130; i++) {
      if (t.every(1)) {
        ticks.add(this.game.getTickCount());
      }
      this.game.runTicks(1);
    }
    assertEquals(List.of(60L, 120L), ticks);
  }

  @Test
  void duringAndAfterSplitTimeInTwo() {
    Timer t = new Timer();
    assertTrue(t.during(0.5));
    assertFalse(t.after(0.5));
    this.game.runTicks(30);
    assertFalse(t.during(0.5));
    assertTrue(t.after(0.5));
  }

  @Test
  void intervalAlternates() {
    Timer t = new Timer();
    StringBuilder pattern = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      pattern.append(t.interval(0.5, 1) ? 'X' : '.');
      this.game.runTicks(15);
    }
    // on for 30 ticks, off for 60: on at ticks 0, 15; off at 30..75
    assertEquals("XX....", pattern.toString());
  }

  @Test
  void anAnswerIsTypedAndFinishedWithEnter() {
    List<KeyCode> spriteKeys = new ArrayList<>();
    Sprite s = new Sprite("red", SceneTest.RED) {
      @Override
      public void whenKeyPressed(KeyCode key) {
        spriteKeys.add(key);
      }
    };
    this.stage.add(s);
    s.ask("Name?");
    assertTrue(s.isAsking());
    for (int cp : "Jörg".codePoints().toArray()) {
      this.game.post(new InputEvent.Typed(cp));
    }
    this.game.post(new InputEvent.Typed('x'));
    this.game.post(new InputEvent.Typed('y'));
    this.game.post(new InputEvent.Typed('z'));
    // Backspace held: one press, then the key repeats
    this.game.post(new InputEvent.Key(KeyCode.BACKSPACE, true));
    this.game.post(new InputEvent.Repeat(KeyCode.BACKSPACE));
    this.game.post(new InputEvent.Repeat(KeyCode.BACKSPACE));
    this.game.post(new InputEvent.Key(KeyCode.BACKSPACE, false));
    this.game.post(new InputEvent.Repeat(KeyCode.ENTER)); // a held Enter does not answer twice
    this.game.runTicks(1);
    assertEquals("Jörg", this.game.drawList().ask().answer().lines().get(0).text());
    assertNull(this.game.drawList().ask().question(), "a sprite's question is in its bubble");
    this.game.post(new InputEvent.Key(KeyCode.ENTER, true));
    this.game.runTicks(1);
    assertFalse(s.isAsking());
    assertEquals("Jörg", s.getAnswer());
    assertTrue(spriteKeys.isEmpty(), "keys went to the answer box");
    assertTrue(this.game.drawList().overlay().isEmpty(), "the question's bubble is gone");
  }

  @Test
  void theStageAsksInACardWhoseCheckButtonFinishesTheAnswer() {
    this.stage.ask("Name?");
    this.game.post(new InputEvent.Typed('A'));
    this.game.runTicks(1);
    AskDraw box = this.game.drawList().ask();
    assertEquals("Name?", box.question().lines().get(0).text());
    assertEquals(480 - 16, box.width(), 1e-9);
    assertEquals(-180 + 8, box.y() - box.height(), 1e-9); // standing on the stage's bottom margin
    this.game.post(new InputEvent.Button(MouseCode.LEFT, true, box.buttonX(), box.buttonY()));
    this.game.runTicks(1);
    assertFalse(this.stage.isAsking());
    assertEquals("A", this.stage.getAnswer());
    assertNull(this.game.drawList().ask());
  }

  @Test
  void repeatsReachNoLearnerCode() {
    List<KeyCode> pressed = new ArrayList<>();
    Sprite s = new Sprite() {
      @Override
      public void whenKeyPressed(KeyCode key) {
        pressed.add(key);
      }
    };
    this.stage.add(s);
    this.game.post(new InputEvent.Key(KeyCode.A, true));
    this.game.post(new InputEvent.Repeat(KeyCode.A));
    this.game.post(new InputEvent.Repeat(KeyCode.A));
    this.game.runTicks(1);
    assertEquals(List.of(KeyCode.A), pressed);
    assertTrue(this.stage.isKeyPressed(KeyCode.A));
  }

  @Test
  void displayShowsABandForSomeSeconds() {
    this.stage.display("Level 2", 1);
    assertEquals(1, this.game.drawList().ui().size());
    assertEquals(480 - 16, this.game.drawList().ui().get(0).width(), 1e-9);
    this.game.runTicks(60);
    assertTrue(this.game.drawList().ui().isEmpty());
  }
}
