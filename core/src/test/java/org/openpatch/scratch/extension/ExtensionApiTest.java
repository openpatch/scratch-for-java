package org.openpatch.scratch.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.Color;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.internal.Game;
import org.openpatch.scratch.internal.draw.ImageDraw;
import org.openpatch.scratch.internal.draw.Item;
import org.openpatch.scratch.internal.draw.ShapeDraw;
import org.openpatch.scratch.internal.input.InputEvent;

/** The public extension API, as a third-party extension would use it, without a window. */
class ExtensionApiTest {

  /** An extension made of hooks the test fills in. */
  static final class TestExtension implements Extension {
    final Consumer<ExtensionContext> install;

    TestExtension(Consumer<ExtensionContext> install) {
      this.install = install;
    }

    @Override
    public String id() {
      return "test";
    }

    @Override
    public String name() {
      return "Test";
    }

    @Override
    public String version() {
      return "1";
    }

    @Override
    public void install(ExtensionContext ctx) {
      this.install.accept(ctx);
    }
  }

  /** What a gamepad extension would offer learners. */
  interface BeepListener {
    default void whenBeep(int n) {}
  }

  Game game;

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
  }

  Stage show() {
    Stage stage = new Stage();
    this.game.setStage(stage);
    return stage;
  }

  @Test
  void hooksRunAtTheirPointsOfTheTick() {
    List<String> log = new ArrayList<>();
    this.game.getExtensions().install(List.of(new TestExtension(ctx -> {
      ctx.onStageCreated(s -> log.add("created"));
      ctx.beforeTick(s -> log.add("before " + ctx.getTickCount()));
      ctx.onKey(e -> log.add("key " + e.key()));
    })));
    Stage stage = new Stage() {
      @Override
      public void run() {
        log.add("run");
      }

      @Override
      public void whenKeyPressed(KeyCode key) {
        log.add("learner key");
      }
    };
    this.game.setStage(stage);
    this.game.post(new InputEvent.Key(KeyCode.A, true));
    this.game.runTicks(2);
    assertEquals(List.of("created", "key A", "learner key", "before 0", "run", "before 1", "run"), log);
  }

  @Test
  void layerDrawersDrawAfterTheCanvasAndBeforeTheSprites() {
    this.game.getExtensions().install(List.of(new TestExtension(ctx -> ctx.addLayerDrawer(Layer.BACKGROUND,
        (stage, canvas) -> {
          canvas.drawImageRegion("costumes/sheet.png", 10, 0, 10, 10, 5, 5, 10, 10);
          canvas.drawRectangle(0, 0, 20, 20, new Color(255, 0, 0));
        }))));
    Stage stage = this.show();
    Sprite s = new Sprite("red", "costumes/red10.png");
    stage.add(s);
    s.setLayer(Layer.BACKGROUND);
    List<Item> items = this.game.drawList().layers().get(Layer.BACKGROUND.ordinal()).items();
    assertEquals(3, items.size());
    ImageDraw tile = assertInstanceOf(ImageDraw.class, items.get(0));
    assertEquals(10, tile.sx());
    assertInstanceOf(ShapeDraw.class, items.get(1));
  }

  @Test
  void dispatchCallsTheStageAndEverySpriteThatListens() {
    List<String> heard = new ArrayList<>();
    class Listener extends Sprite implements BeepListener {
      @Override
      public void whenBeep(int n) {
        heard.add("sprite " + n);
      }
    }
    ExtensionContext[] context = new ExtensionContext[1];
    this.game.getExtensions().install(List.of(new TestExtension(ctx -> {
      context[0] = ctx;
      ctx.beforeTick(stage -> ctx.dispatch(stage, BeepListener.class, l -> l.whenBeep(7)));
    })));
    Stage stage = this.show();
    stage.add(new Listener());
    stage.add(new Sprite());
    this.game.runTicks(1);
    assertEquals(List.of("sprite 7"), heard);
    // game-thread methods from another thread fail at once
    Thread other = new Thread(() -> assertThrows(IllegalStateException.class,
        () -> context[0].dispatch(stage, BeepListener.class, l -> l.whenBeep(1))));
    other.start();
    assertTrue(joined(other));
  }

  @Test
  void aHookThatThrowsIsSwitchedOffAndTheGameGoesOn() {
    int[] calls = {0};
    this.game.getExtensions().install(List.of(new TestExtension(ctx -> ctx.beforeTick(s -> {
      calls[0]++;
      throw new IllegalStateException("broken");
    }))));
    this.show();
    this.game.runTicks(5);
    assertEquals(1, calls[0]);
    assertEquals(null, this.game.getStopped());
    assertEquals(5, this.game.getTickCount());
  }

  @Test
  void attachmentsAreOnePerStageAndSprite() {
    Stage stage = this.show();
    Sprite s = new Sprite("red", "costumes/red10.png");
    stage.add(s);
    StringBuilder a = stage.extension(StringBuilder.class, StringBuilder::new);
    assertSame(a, stage.extension(StringBuilder.class, StringBuilder::new));
    StringBuilder mine = s.extension(StringBuilder.class, StringBuilder::new);
    Sprite clone = s.createClone();
    assertFalse(mine == clone.extension(StringBuilder.class, StringBuilder::new), "a clone gets its own");
  }

  @Test
  void theInputSnapshotIsHandedOverWithTheBatch() {
    ExtensionContext.InputKey<int[]> buttons = ExtensionContext.InputKey.of("buttons", () -> new int[1],
        (from, into) -> into[0] = from[0]);
    int[] seen = {-1};
    int[] device = {0};
    this.game.getExtensions().install(List.of(new TestExtension(ctx -> {
      ctx.onInputPoll(snapshot -> snapshot.get(buttons)[0] = device[0]);
      ctx.beforeTick(stage -> seen[0] = ctx.input(buttons)[0]);
    })));
    this.show();
    device[0] = 3;
    // what the window thread does each frame
    this.game.getExtensions().poll();
    this.game.getExtensions().handOver();
    this.game.runTicks(1);
    assertEquals(3, seen[0]);
    device[0] = 4; // polled but not handed over yet: the game still sees 3
    this.game.getExtensions().poll();
    this.game.runTicks(1);
    assertEquals(3, seen[0]);
  }

  @Test
  void anOverlayDrawerIsInTheDrawList() {
    this.game.getExtensions().install(List.of(new TestExtension(
        ctx -> ctx.addOverlayDrawer((stage, canvas) -> canvas.drawText("pin", 0, 0, 12, new Color(0, 0, 0))))));
    this.show();
    assertEquals(1, this.game.drawList().overlayItems().size());
  }

  @Test
  void aCanvasCannotBeKeptForLater() {
    Canvas[] kept = new Canvas[1];
    this.game.getExtensions().install(List.of(new TestExtension(
        ctx -> ctx.addLayerDrawer(Layer.MAIN, (stage, canvas) -> kept[0] = canvas))));
    this.show();
    this.game.drawList();
    assertThrows(IllegalStateException.class, () -> kept[0].drawRectangle(0, 0, 1, 1, new Color(0, 0, 0)));
  }

  @Test
  void aLearnerExceptionInADispatchedListenerStopsTheGameButNotTheExtension() {
    class Listener extends Sprite implements BeepListener {
      @Override
      public void whenBeep(int n) {
        throw new IllegalStateException("learner bug");
      }
    }
    int[] beforeTicks = {0};
    this.game.getExtensions().install(List.of(new TestExtension(ctx -> ctx.beforeTick(stage -> {
      beforeTicks[0]++;
      ctx.dispatch(stage, BeepListener.class, l -> l.whenBeep(1));
    }))));
    Stage stage = this.show();
    stage.add(new Listener());
    this.game.runTicks(3);
    assertEquals(1, beforeTicks[0]);
    assertTrue(this.game.getStopped().message().contains("learner bug"));
  }

  private static boolean joined(Thread t) {
    try {
      t.join(2000);
      return !t.isAlive();
    } catch (InterruptedException e) {
      return false;
    }
  }
}
