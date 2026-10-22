package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.DrawList;
import org.openpatch.scratch.internal.draw.SpriteDraw;
import org.openpatch.scratch.internal.input.InputEvent;

/** The scene without a window: ticks, clones, layers, touching, pens, input, learner errors. */
class SceneTest {

  static final String RED = "costumes/red10.png";
  static final String LEFT4 = "costumes/left4.png";

  Game game;
  Stage stage;
  final List<String> log = new ArrayList<>();

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
    this.stage = new Stage() {
      @Override
      public void run() {
        SceneTest.this.log.add("stage");
      }
    };
    this.game.setStage(this.stage);
  }

  class Logged extends Sprite {
    final String name;

    Logged(String name) {
      super("red", RED);
      this.name = name;
    }

    @Override
    public void run() {
      SceneTest.this.log.add(this.name);
    }
  }

  @Test
  void theStageRunsFirstThenTheSpritesBackToFront() {
    Logged main = new Logged("main"), back = new Logged("back"), ui = new Logged("ui");
    this.stage.add(main);
    this.stage.add(ui);
    this.stage.add(back);
    ui.setLayer(Layer.UI);
    back.setLayer(Layer.BACKGROUND);
    this.game.runTicks(1);
    assertEquals(List.of("stage", "back", "main", "ui"), this.log);
  }

  @Test
  void aSpriteRemovedDuringATickDoesNotRunAnyMore() {
    Logged second = new Logged("second");
    Sprite first = new Sprite() {
      @Override
      public void run() {
        second.remove();
      }
    };
    this.stage.add(first);
    this.stage.add(second);
    this.game.runTicks(1);
    assertEquals(List.of("stage"), this.log);
  }

  static class Bullet extends Sprite {
    int speed = 3;
    int constructed;
    boolean startedAsClone;

    Bullet() {
      super("red", RED);
      this.constructed++;
    }

    @Override
    public void whenStartsAsClone() {
      this.startedAsClone = true;
    }
  }

  @Test
  void aCloneCopiesTheVariablesWithoutRunningTheConstructor() {
    Bullet original = new Bullet();
    original.speed = 7;
    original.setPosition(30, 40);
    this.stage.add(original);
    Bullet clone = original.createClone();
    assertEquals(7, clone.speed);
    assertEquals(1, clone.constructed);
    assertTrue(clone.startedAsClone);
    assertTrue(clone.isClone());
    assertFalse(original.isClone());
    assertEquals(30, clone.getX());
    assertSame(this.stage, clone.getStage());
    // behind the original, as in Scratch
    assertEquals(List.of(clone, original), this.stage.getAll());
    clone.deleteThisClone();
    original.deleteThisClone();
    assertEquals(List.of(original), this.stage.getAll());
  }

  @Test
  void goToFrontLayerMeansTheFrontOfTheSpritesOwnLayer() {
    Sprite a = new Sprite("red", RED), b = new Sprite("red", RED), c = new Sprite("red", RED);
    this.stage.add(a);
    this.stage.add(b);
    this.stage.add(c);
    c.setLayer(Layer.FOREGROUND);
    a.goToFrontLayer();
    assertEquals(List.of(b, a, c), this.stage.getAll());
    a.goLayersBackwards(1);
    assertEquals(List.of(a, b, c), this.stage.getAll());
  }

  @Test
  void touchingEdgesIsNotTouching() {
    Sprite a = new Sprite("red", RED), b = new Sprite("red", RED);
    this.stage.add(a);
    this.stage.add(b);
    b.setX(10);
    assertFalse(a.isTouchingSprite(b));
    b.setX(9);
    assertTrue(a.isTouchingSprite(b));
    b.hide();
    assertFalse(a.isTouchingSprite(b));
    b.show();
    b.setLayer(Layer.UI);
    assertFalse(a.isTouchingSprite(b), "the UI never touches the world");
  }

  @Test
  void theHitboxWrapsThePaintedPixels() {
    Sprite s = new Sprite("left", LEFT4);
    this.stage.add(s);
    Bounds b = s.getHitbox().getBounds();
    // 20 wide, centred on 0: painted columns 0..3 are x = -10..-6
    assertEquals(-10, b.x(), 1e-9);
    assertEquals(4, b.width(), 1e-9);
    assertEquals(-5, b.y(), 1e-9);
    assertEquals(10, b.height(), 1e-9);
  }

  @Test
  void facingLeftWithTheLeftRightStyleMirrorsTheHitbox() {
    Sprite s = new Sprite("left", LEFT4);
    this.stage.add(s);
    s.setRotationStyle(RotationStyle.LEFT_RIGHT);
    s.setDirection(270);
    Bounds b = s.getHitbox().getBounds();
    assertEquals(6, b.x(), 1e-9);
    assertEquals(4, b.width(), 1e-9);
  }

  @Test
  void turningAllAroundTurnsTheHitboxClockwise() {
    Sprite s = new Sprite("left", LEFT4);
    this.stage.add(s);
    s.setDirection(180); // a quarter turn clockwise from 90: the left columns end up on top
    Bounds b = s.getHitbox().getBounds();
    assertEquals(-5, b.x(), 1e-9);
    assertEquals(10, b.width(), 1e-9);
    assertEquals(6, b.y(), 1e-9);
    assertEquals(4, b.height(), 1e-9);
  }

  @Test
  void aSpritesSizeScalesItsHitbox() {
    Sprite s = new Sprite("red", RED);
    this.stage.add(s);
    s.setSize(200);
    assertEquals(20, s.getHitbox().getBounds().width(), 1e-9);
    assertEquals(20, s.getWidth(), 1e-9);
  }

  @Test
  void moveFollowsScratchsDirections() {
    Sprite s = new Sprite();
    this.stage.add(s);
    s.move(10);
    assertEquals(10, s.getX(), 1e-9);
    s.setDirection(0);
    s.move(10);
    assertEquals(10, s.getY(), 1e-9);
    s.pointTowards(new Vector2(10, -100));
    assertEquals(180, s.getDirection(), 1e-9);
  }

  @Test
  void aGlideOfOneSecondTakesSixtyTicks() {
    Sprite s = new Sprite();
    this.stage.add(s);
    s.glide(1, 60, 0);
    this.game.runTicks(30);
    assertEquals(30, s.getX(), 1e-9);
    assertTrue(s.isGliding());
    this.game.runTicks(30);
    assertEquals(60, s.getX(), 1e-9);
    assertFalse(s.isGliding());
  }

  @Test
  void penAndStampsChangeTheCanvasAtOnceInProgramOrder() {
    Sprite s = new Sprite("red", RED);
    this.stage.add(s);
    s.stamp();
    assertEquals(0xffff0000, this.stage.canvas(Layer.BACKGROUND).at(0.5, 0.5));
    this.stage.eraseAll();
    assertEquals(0, this.stage.canvas(Layer.BACKGROUND).at(0.5, 0.5));
    s.setX(100);
    s.stamp();
    // an erase removes exactly what came before it
    assertEquals(0xffff0000, this.stage.canvas(Layer.BACKGROUND).at(100.5, 0.5));
    s.getPen().setColor(new Color(0, 255, 0));
    s.getPen().setSize(3);
    s.getPen().down();
    s.setX(150);
    assertEquals(0xff00ff00, this.stage.canvas(Layer.BACKGROUND).at(130, 0));
  }

  @Test
  void aKeyPressReachesTheStageAndEverySpriteOnce() {
    List<KeyCode> seen = new ArrayList<>();
    Sprite s = new Sprite() {
      @Override
      public void whenKeyPressed(KeyCode key) {
        seen.add(key);
      }
    };
    this.stage.add(s);
    this.game.post(new InputEvent.Key(KeyCode.SPACE, true));
    this.game.runTicks(3);
    assertEquals(List.of(KeyCode.SPACE), seen);
    assertTrue(s.isKeyPressed(KeyCode.SPACE));
  }

  @Test
  void aNewStageGetsNoReleaseForAPressOnTheOldOne() {
    List<String> seen = new ArrayList<>();
    this.game.post(new InputEvent.Key(KeyCode.A, true));
    this.game.runTicks(1);
    Stage next = new Stage() {
      @Override
      public void whenKeyReleased(KeyCode key) {
        seen.add("released " + key);
      }
    };
    this.game.setStage(next);
    this.game.post(new InputEvent.Key(KeyCode.A, false));
    this.game.runTicks(1);
    assertEquals(List.of(), seen);
    assertFalse(next.isKeyPressed(KeyCode.A));
  }

  @Test
  void aKeyReleasedDuringATransitionIsNotHeldAfterIt() {
    List<String> seen = new ArrayList<>();
    this.game.post(new InputEvent.Key(KeyCode.RIGHT, true));
    this.game.runTicks(1);
    Stage next = new Stage() {
      @Override
      public void whenKeyReleased(KeyCode key) {
        seen.add("released " + key);
      }
    };
    this.game.transition(() -> next, "fade", null, 0.5);
    this.game.runTicks(2);
    this.game.post(new InputEvent.Key(KeyCode.RIGHT, false));
    this.game.runTicks(1);
    assertTrue(this.game.isTransitioning());
    this.game.runTicks(40);
    assertFalse(this.game.isTransitioning());
    assertFalse(next.isKeyPressed(KeyCode.RIGHT));
    assertEquals(List.of(), seen);
  }

  @Test
  void aTransitionShowsTheOldStageAsItWasBeforeTheSupplierRan() {
    Logged hero = new Logged("hero");
    this.stage.add(hero);
    this.game.runTicks(1);
    Stage next = new Stage();
    // the supplier takes the sprite along to the new stage, as a warp does
    this.game.transition(() -> {
      next.add(hero);
      return next;
    }, "fade", null, 0.5);
    this.game.runTicks(1);
    DrawList from = this.game.drawList().transition().from();
    long sprites = from.layers().stream().flatMap(l -> l.items().stream()).filter(i -> i instanceof SpriteDraw).count();
    assertEquals(1, sprites, "the old stage's picture still shows the sprite");
  }

  @Test
  void onlyTheFrontmostSpriteUnderThePointerIsClicked() {
    List<String> clicked = new ArrayList<>();
    class Clickable extends Sprite {
      final String name;

      Clickable(String name) {
        super("red", RED);
        this.name = name;
      }

      @Override
      public void whenClicked() {
        clicked.add(this.name);
      }
    }
    this.stage.add(new Clickable("back"));
    this.stage.add(new Clickable("front"));
    this.game.post(new InputEvent.Button(MouseCode.LEFT, true, 1, 1));
    this.game.runTicks(1);
    assertEquals(List.of("front"), clicked);
  }

  @Test
  void theMouseStaysDownWhileDragging() {
    this.game.post(new InputEvent.Button(MouseCode.LEFT, true, 0, 0));
    this.game.post(new InputEvent.Move(30, 30));
    this.game.runTicks(1);
    assertTrue(this.stage.isMouseDown());
    this.game.post(new InputEvent.Button(MouseCode.LEFT, false, 30, 30));
    this.game.runTicks(1);
    assertFalse(this.stage.isMouseDown());
  }

  @Test
  void aLearnerExceptionStopsTheGameAndNamesTheLearnersLine() {
    int[] runs = {0};
    Sprite s = new Sprite() {
      @Override
      public void run() {
        runs[0]++;
        if (runs[0] == 2) {
          throw new IllegalStateException("boom");
        }
      }
    };
    this.stage.add(s);
    this.game.runTicks(10);
    assertEquals(2, runs[0]);
    assertNotNull(this.game.getStopped());
    assertTrue(this.game.getStopped().learnerFrame().getClassName().startsWith(SceneTest.class.getName()));
    assertTrue(this.game.getStopped().message().contains("boom"));
  }

  @Test
  void theDrawListHasTheSpritesLayerByLayerAndCopiesTheirState() {
    Sprite a = new Sprite("red", RED), b = new Sprite("red", RED);
    this.stage.add(a);
    this.stage.add(b);
    b.setLayer(Layer.BACKGROUND);
    a.setPosition(5, 6);
    a.setSize(50);
    this.game.runTicks(1);
    DrawList list = this.game.drawList();
    assertEquals(1, list.tick());
    SpriteDraw drawnA = (SpriteDraw) list.layers().get(Layer.MAIN.ordinal()).items().get(0);
    assertEquals(5, drawnA.x());
    assertEquals(0.5, drawnA.scaleX());
    assertEquals(1, list.layers().get(Layer.BACKGROUND.ordinal()).items().size());
    a.setPosition(100, 100);
    assertEquals(5, drawnA.x(), "the draw list does not change with the sprite");
    a.hide();
    assertTrue(this.game.drawList().layers().get(Layer.MAIN.ordinal()).items().isEmpty());
  }

  @Test
  void colourSensingSeesTheStageColourAndOtherSprites() {
    Sprite me = new Sprite("red", RED), other = new Sprite("left", LEFT4);
    this.stage.add(me);
    this.stage.add(other);
    assertTrue(me.isTouchingColor(new Color(255, 255, 255)), "the white stage around nothing else");
    other.setX(100);
    assertFalse(me.isTouchingColor(new Color(0, 0, 255)));
    other.setX(12); // its blue columns now cover x = 2..6, under me
    assertTrue(me.isTouchingColor(new Color(0, 0, 255)));
    assertTrue(me.isColorTouchingColor(new Color(255, 0, 0), new Color(0, 0, 255)));
    assertFalse(me.isColorTouchingColor(new Color(0, 255, 0), new Color(0, 0, 255)));
    other.hide();
    assertFalse(me.isTouchingColor(new Color(0, 0, 255)));
  }

  @Test
  void colourSensingSeesPenStrokesInStatementOrder() {
    Sprite me = new Sprite("red", RED);
    Sprite painter = new Sprite();
    this.stage.add(me);
    this.stage.add(painter);
    Color green = new Color(0, 255, 0);
    painter.getPen().setColor(green);
    painter.getPen().setSize(4);
    painter.setPosition(-20, 0);
    painter.getPen().down();
    painter.setPosition(20, 0);
    assertTrue(me.isTouchingColor(green), "the line drawn by the statement before");
    this.stage.eraseAll();
    assertFalse(me.isTouchingColor(green), "erased by the statement before");
  }

  @Test
  void theGhostEffectHidesOthersButNotTheSensingSprite() {
    Sprite me = new Sprite("red", RED), other = new Sprite("red", RED);
    this.stage.add(me);
    this.stage.add(other);
    other.setColorEffect(133.3333); // two thirds of the way round: red becomes blue
    assertTrue(me.isTouchingColor(new Color(0, 0, 255)));
    other.setTransparency(100);
    assertFalse(me.isTouchingColor(new Color(0, 0, 255)));
    me.setTransparency(100);
    other.setTransparency(0);
    assertTrue(me.isTouchingColor(new Color(0, 0, 255)));
  }

  @Test
  void pixelsOfTheStageShowWhatColourSensingSees() {
    Sprite s = new Sprite("red", RED);
    this.stage.add(s);
    s.setColorEffect(66.6667); // a third of the way round: red becomes green
    Pixels p = Pixels.of(this.stage);
    assertEquals(480, p.getWidth());
    assertEquals(new Color(255, 255, 255), p.getColor(0, 0));
    assertEquals(new Color(0, 255, 0), p.getColor(240, 180));
  }

  @Test
  void theColourEffectTintsWhiteLikeScratch() {
    // Scratch forces white to 9% saturation before the turn: a quarter round gives (244, 255, 232)
    Sprite s = new Sprite("white", "costumes/white8.png");
    this.stage.add(s);
    s.setColorEffect(50);
    Pixels p = Pixels.of(this.stage);
    assertEquals(new Color(244, 255, 232), p.getColor(240, 180));
  }
}
