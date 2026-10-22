package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;

/**
 * Clone lifecycle, removal, broadcasts and monitors (6.x {@code compatibility/probes/LifecycleProbe},
 * now headless). The online IDE runs the same probe; the keys and expected values are 6.x's
 * {@code lifecycle-expected.txt}. Its last checks went through copy constructors 6.x used for
 * cloning (Pen, Text, Shaders) and a Sorting class; 7.0 has none of them, so those keys check what
 * they guarded through the public API instead: a clone's own pen and shaders, equal colours with
 * equal hashes, sorting off by default.
 */
class LifecycleParityTest {

  static final List<String> EXPECTED = List.of(
      "clone.constructor=1", "clone.score=8", "original.score=7", "clone.starts=1", "clone.shared=2",
      "clone.timer=true", "clone.pen=true", "clone.x=true", "clone.width=true", "broadcast.original=1",
      "broadcast.clone=1", "removed.messages=1", "removed.hook=1", "original.messages=2", "readded.messages=2",
      "removed.again=2", "pen.color=true", "text.copy=true", "shaders.copy=true", "color.hash=true", "sorting=true",
      "ready");

  Game game;
  final List<String> lines = new ArrayList<>();

  @BeforeEach
  void setUp() {
    this.game = Game.headless(600, 240);
    ProbeSprite.constructions = 0;
  }

  @AfterEach
  void tearDown() {
    this.game.setStage(null);
  }

  void p(String key, Object value) {
    this.lines.add(key + "=" + value);
  }

  static class ProbeSprite extends Sprite {
    static int constructions;
    int score = 7;
    int[] shared = {1};
    int starts;
    int messages;
    int removals;

    ProbeSprite() {
      constructions++;
      this.addCostume("slimeGreen");
      this.setPosition(30, -20);
      this.getPen().setSize(5);
    }

    @Override
    public void whenStartsAsClone() {
      if (!this.isClone() || this.getStage() == null) {
        throw new RuntimeException("clone hook ordering");
      }
      this.starts++;
      this.score++;
    }

    @Override
    public void whenIReceive(String message) {
      this.messages++;
    }

    @Override
    public void whenRemovedFromStage() {
      if (this.getStage() != null) {
        throw new RuntimeException("removal hook ordering");
      }
      this.removals++;
    }

    @Override
    public void run() {
      this.changeX(1);
    }
  }

  @Test
  void clonesRemovalBroadcastsAndMonitorsBehaveAsRecorded() {
    int[] frames = {0};
    Stage stage = new Stage() {
      @Override
      public void run() {
        frames[0]++;
      }
    };
    this.game.setStage(stage);

    ProbeSprite original = new ProbeSprite();
    stage.add(original);
    ProbeSprite copy = original.createClone();
    copy.getPen().setSize(9);
    copy.shared[0] = 2;
    p("clone.constructor", ProbeSprite.constructions); // a clone is copied, not constructed
    p("clone.score", copy.score);
    p("original.score", original.score);
    p("clone.starts", copy.starts);
    p("clone.shared", original.shared[0]); // fields are copied shallowly, as in Scratch's clones
    p("clone.timer", copy.getTimer() != original.getTimer());
    p("clone.pen", original.getPen().getSize() == 5);
    p("clone.x", copy.getX() == 30);
    p("clone.width", copy.getWidth() == original.getWidth());

    stage.broadcast("first");
    p("broadcast.original", original.messages);
    p("broadcast.clone", copy.messages);
    copy.deleteThisClone();
    copy.deleteThisClone(); // twice: nothing more happens
    original.deleteThisClone(); // not a clone: stays
    stage.broadcast("second");
    p("removed.messages", copy.messages);
    p("removed.hook", copy.removals);
    p("original.messages", original.messages);
    stage.add(copy);
    stage.broadcast("third");
    p("readded.messages", copy.messages);
    stage.remove(copy);
    p("removed.again", copy.removals);

    // 6.x: new Pen(pen) copied a pen; 7.0: a clone's pen is its own
    original.getPen().setColor(255, 0, 0);
    ProbeSprite second = original.createClone();
    second.getPen().changeColor(30);
    p("pen.color", original.getPen().getColor().getRed() == 255);
    // 6.x: new Text(text) copied a text; 7.0: a clone's bubble is its own
    original.say("hello");
    ProbeSprite third = original.createClone();
    third.say("bye");
    p("text.copy", original.getText().getText().equals("hello"));
    // 6.x: new Shaders(shaders) copied; 7.0: a clone's shaders are its own list
    p("shaders.copy", second.getShaders() != original.getShaders()
        && second.getShaders().getCurrentIndex() == original.getShaders().getCurrentIndex());
    // 6.x checked the exact hash; what learners rely on is that equal colours hash equally
    p("color.hash", new Color(255, 128, 0).hashCode() == new Color(255, 128, 0).hashCode());
    // no sorting unless asked for: the order is the order of adding, and a clone goes just behind
    // its original (as in Scratch)
    p("sorting", stage.getAll().equals(List.of(second, third, original)));

    // monitors, a failing one included, must not stop the game
    stage.showVariable("frames", () -> frames[0]);
    stage.showVariable("decimal", () -> 1.234);
    stage.showVariable("array", () -> original.shared);
    stage.showVariable("broken", () -> {
      throw new RuntimeException("supplier failure");
    });
    original.showVariable("score", () -> original.score);
    stage.showVariable("timer", () -> original.getTimer().after(0.1));
    stage.showVariable("unused", () -> 99);
    stage.hideVariable("unused");
    original.say("timed", 0.1);
    original.glide(0.1, 90, -20);
    this.game.runTicks(30);
    this.game.drawList();
    assertNull(this.game.getStopped(), "a failing monitor stopped the game");
    assertEquals(-20, original.getY(), 1e-9, "the glide arrived (x moves on: run() adds 1 a tick)");
    assertTrue(this.game.drawList().overlay().stream().noneMatch(t -> t.layout().lines().stream()
        .anyMatch(l -> l.text().contains("timed"))), "a bubble said for 0.1 s is gone after 0.5 s");
    this.lines.add("ready");
    assertEquals(String.join("\n", EXPECTED), String.join("\n", this.lines));
  }
}
