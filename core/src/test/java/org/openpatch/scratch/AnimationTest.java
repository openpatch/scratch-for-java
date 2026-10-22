package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.SpriteDraw;

/** Animations and a size of its own, now on every sprite. */
class AnimationTest {

  Game game;
  Stage stage;

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
    this.stage = new Stage();
    this.game.setStage(this.stage);
  }

  /** Red of the costume's top-left pixel, which tells the frame apart. */
  static int red(Sprite s) {
    SpriteDraw d = s.toDraw();
    return (d.costume().get(0, 0) >> 16) & 0xff;
  }

  @Test
  void aSheetAnimationShowsItsFramesInOrderEveryInterval() {
    List<Integer> seen = new ArrayList<>();
    Sprite s = new Sprite() {
      @Override
      public void run() {
        this.playAnimation("column", false);
        seen.add(red(this));
      }
    };
    s.addAnimation("column", "costumes/sheet.png", 10, 10, 0, 4); // frames 0 and 4: red 0 and 80
    s.setAnimationInterval(2 / 60.0);
    this.stage.add(s);
    this.game.runTicks(6);
    assertEquals(List.of(0, 0, 80, 80, 0, 0), seen);
  }

  @Test
  void numberedFilesFromOneNumberToAnotherAlsoCountDown() {
    List<Integer> seen = new ArrayList<>();
    Sprite s = new Sprite() {
      @Override
      public void run() {
        this.playAnimation("up", false);
        seen.add(red(this));
      }
    };
    s.addAnimation("up", "costumes/walk%d.png", 2, 3); // red 100, 150
    s.addAnimation("down", "costumes/walk%d.png", 3, 1);
    s.setAnimationInterval(1 / 60.0);
    this.stage.add(s);
    this.game.runTicks(2);
    assertEquals(List.of(100, 150), seen);
    s.switchCostume("down#0");
    assertEquals(150, red(s));
    s.switchCostume("down#2");
    assertEquals(50, red(s));
  }

  @Test
  void anAnimationPlayedOnceStopsAtItsLastFrame() {
    Sprite s = new Sprite() {
      @Override
      public void run() {
        this.playAnimation("walk", true);
      }
    };
    s.addAnimation("walk", "costumes/walk%d.png", 3);
    s.setAnimationInterval(1 / 60.0);
    this.stage.add(s);
    this.game.runTicks(10);
    assertEquals(150, red(s));
    assertTrue(s.isAnimationPlayed());
    assertEquals(2, s.getAnimationFrame());
  }

  @Test
  void aSizeOfItsOwnStretchesTheCostumeAndTheHitbox() {
    Sprite s = new Sprite("red", SceneTest.RED);
    this.stage.add(s);
    s.setWidth(100);
    s.setHeight(30);
    s.setNineSlice(2, 2, 2, 2);
    assertEquals(100, s.getHitbox().getBounds().width(), 1e-9);
    assertEquals(30, s.getHitbox().getBounds().height(), 1e-9);
    SpriteDraw d = s.toDraw();
    assertNotNull(d.nineSlice());
    assertEquals(100, d.width(), 1e-9);
    assertEquals(10, d.scaleX(), 1e-9);
    s.setWidth(0);
    s.setHeight(0);
    assertEquals(10, s.getWidth(), 1e-9);
  }

  @Test
  void aNineSlicedBarAsHighAsItsCapsStretchesItsEmptyMiddle() {
    // the ui demo's bar: 24 px high, 12 + 12 at the top and bottom, drawn 40 high
    double[][] v = SpriteDraw.slices(24, 12, 12, 40, 1);
    assertArrayEquals(new double[] {0, 12, 28, 40}, v[0], 1e-9);
    assertArrayEquals(new double[] {0, 0.5, 0.5, 1}, v[1], 1e-9);
  }

  @Test
  void nineSliceInsetsGrowWithTheSpritesSize() {
    double[][] h = SpriteDraw.slices(96, 24, 24, 100, 0.7);
    assertArrayEquals(new double[] {0, 24 * 0.7, 100 - 24 * 0.7, 100}, h[0], 1e-9);
    assertArrayEquals(new double[] {0, 0.25, 0.75, 1}, h[1], 1e-9);
    Sprite s = new Sprite("red", SceneTest.RED);
    s.setSize(70);
    s.setWidth(100);
    s.setNineSlice(2, 2, 2, 2);
    assertEquals(0.7, s.toDraw().sliceScale(), 1e-9);
  }

  @Test
  void nineSliceInsetsShrinkTogetherBelowTheirSize() {
    double[][] h = SpriteDraw.slices(96, 24, 24, 30, 1);
    assertArrayEquals(new double[] {0, 15, 15, 30}, h[0], 1e-9);
  }

  @Test
  void colourSensingSeesTheNineSliceAsTheScreenDoes() {
    // left4: 20 × 10, its four left columns blue; drawn 200 wide with a left inset of 4
    Sprite s = new Sprite("left", SceneTest.LEFT4);
    this.stage.add(s);
    s.setWidth(200);
    s.setNineSlice(0, 0, 0, 4);
    Pixels pixels = Pixels.of(this.stage);
    int blue = 0xff0000ff;
    for (int x = 140; x < 144; x++) {
      assertEquals(blue, pixels.getArgb(x, 180), "inset column " + x);
    }
    assertNotEquals(blue, pixels.getArgb(144, 180), "stretched whole, the blue would reach 179");
    assertNotEquals(blue, pixels.getArgb(170, 180));
  }

  @Test
  void insetsThatDoNotFitTheCostumeStretchItWhole() {
    double[][] h = SpriteDraw.slices(10, 6, 6, 50, 1);
    assertArrayEquals(new double[] {0, 50}, h[0], 1e-9);
    assertArrayEquals(new double[] {0, 1}, h[1], 1e-9);
  }
}
