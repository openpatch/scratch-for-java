package org.openpatch.scratch.gamepad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Vector2;
import org.openpatch.scratch.internal.Game;
import org.lwjgl.glfw.GLFWGamepadState;

/** The gamepad extension on a headless game, with made-up controller state. */
class GamepadTest {

  Game game;
  Stage stage;
  Events events;

  /** Writes fixed state into whatever the extension polls. */
  static class FakeSource implements GamepadSource {
    GamepadState state = new GamepadState();

    @Override
    public void read(GamepadState into) {
      into.copyFrom(this.state);
    }
  }

  FakeSource source;

  /** A stage and a sprite that both record what they are told. */
  static class Events extends Stage implements GamepadListener {
    final List<String> log = new ArrayList<>();

    @Override
    public void whenGamepadButtonPressed(GamepadButton button) {
      this.log.add("pressed " + button);
    }

    @Override
    public void whenGamepadButtonReleased(GamepadButton button) {
      this.log.add("released " + button);
    }

    @Override
    public void whenGamepadConnected(int player) {
      this.log.add("connected " + player);
    }
  }

  static class Player extends Sprite implements GamepadListener {
    boolean aSeenInRun;

    Player() {}

    @Override
    public void run() {
      this.aSeenInRun |= Gamepad.isButtonPressed(GamepadButton.A);
    }
  }

  @BeforeEach
  void setUp() {
    this.game = Game.headless(480, 360);
    this.game.getExtensions().install(List.of(new GamepadExtension()));
    this.source = new FakeSource();
    GamepadExtension.useSource(this.source);
    this.stage = new Stage();
    this.game.setStage(this.stage);
    this.events = new Events();
  }

  @AfterEach
  void tearDown() {
    // the game thread's state lives past the test: empty it, or the next test's first tick sees
    // buttons it never pressed
    GamepadExtension.now.clear();
    GamepadExtension.useSource(new FakeSource());
  }

  /** The window thread's side of one batch: poll, hand over, then tick. */
  void tick() {
    this.game.getExtensions().poll();
    this.game.getExtensions().handOver();
    this.game.runTicks(1);
  }

  void connect(int player) {
    this.source.state.setConnected(player, true);
  }

  @Test
  void asksNothingWhenNoControllerIsThere() {
    this.tick();
    assertFalse(Gamepad.isConnected());
    assertFalse(Gamepad.isButtonPressed(GamepadButton.A));
    assertEquals(new Vector2(0, 0), Gamepad.getStick(GamepadStick.LEFT));
    assertEquals(0, Gamepad.getTrigger(GamepadTrigger.LEFT));
  }

  @Test
  void asksThePlayerItIsToldAbout() {
    this.connect(2);
    this.source.state.setPressed(2, GamepadButton.START, true);
    this.source.state.setStick(2, GamepadStick.LEFT, -0.5, 0.75);
    this.source.state.setTrigger(2, GamepadTrigger.RIGHT, 0.25);
    this.tick();

    assertTrue(Gamepad.isConnected());
    assertTrue(Gamepad.isConnected(2));
    assertFalse(Gamepad.isConnected(1));
    assertTrue(Gamepad.isButtonPressed(2, GamepadButton.START));
    // "any controller" sees player 2's START too
    assertTrue(Gamepad.isButtonPressed(GamepadButton.START));
    assertEquals(-0.5, Gamepad.getStick(2, GamepadStick.LEFT).getX(), 1e-9);
    assertEquals(0.75, Gamepad.getStick(2, GamepadStick.LEFT).getY(), 1e-9);
    assertEquals(0.25, Gamepad.getTrigger(2, GamepadTrigger.RIGHT), 1e-9);
    assertThrows(IllegalArgumentException.class, () -> Gamepad.isConnected(5));
    assertThrows(IllegalArgumentException.class, () -> Gamepad.isConnected(0));
  }

  @Test
  void withoutAPlayerEveryControllerAnswers() {
    this.connect(1);
    this.connect(3);
    this.source.state.setPressed(3, GamepadButton.Y, true);
    this.source.state.setStick(1, GamepadStick.RIGHT, 0.2, -0.3);
    this.source.state.setTrigger(1, GamepadTrigger.LEFT, 0.4);
    this.source.state.setTrigger(3, GamepadTrigger.LEFT, 0.9);
    this.tick();

    // any controller answers for buttons, the first connected (player 1) for sticks and triggers
    assertTrue(Gamepad.isButtonPressed(GamepadButton.Y));
    assertEquals(0.2, Gamepad.getStick(GamepadStick.RIGHT).getX(), 1e-9);
    assertEquals(-0.3, Gamepad.getStick(GamepadStick.RIGHT).getY(), 1e-9);
    assertEquals(0.4, Gamepad.getTrigger(GamepadTrigger.LEFT), 1e-9);
    assertEquals(0.9, Gamepad.getTrigger(3, GamepadTrigger.LEFT), 1e-9);
  }

  @Test
  void tellsTheStageAndSpritesAboutPressesReleasesAndConnections() {
    Player player = new Player();
    this.game.setStage(this.events);
    this.events.add(player);

    this.connect(1);
    this.tick();
    this.source.state.setPressed(1, GamepadButton.A, true);
    this.tick();
    this.source.state.setPressed(1, GamepadButton.A, false);
    this.tick();
    this.source.state.setConnected(1, false);
    this.tick();

    assertEquals(List.of("connected 1", "pressed A", "released A"), this.events.log);
    // the press reached run() in the same tick as the listener
    assertTrue(player.aSeenInRun);
  }

  @Test
  void pressesComeOnceAndOnlyWhileHeld() {
    this.game.setStage(this.events);
    this.connect(1);
    this.tick();
    this.source.state.setPressed(1, GamepadButton.B, true);
    this.tick();
    this.tick(); // still held: no second event
    this.source.state.setPressed(1, GamepadButton.B, false);
    this.tick();
    this.tick(); // still up: no second event

    assertEquals(List.of("connected 1", "pressed B", "released B"), this.events.log);
  }

  @Test
  void playersAnswerSeparately() {
    this.game.setStage(this.events);
    this.connect(1);
    this.connect(2);
    this.tick();
    this.source.state.setPressed(1, GamepadButton.A, true);
    this.tick();
    this.source.state.setPressed(2, GamepadButton.A, true);
    this.tick();
    this.source.state.setPressed(1, GamepadButton.A, false);
    this.tick();

    // every player's press is its own event, in tick order
    assertEquals(List.of("connected 1", "connected 2", "pressed A", "pressed A", "released A"),
        this.events.log);
    assertTrue(Gamepad.isButtonPressed(2, GamepadButton.A));
    assertFalse(Gamepad.isButtonPressed(1, GamepadButton.A));
  }

  @Test
  void mapsGlfwStateToStageCoordinates() {
    GLFWGamepadState raw = GLFWGamepadState.create();
    raw.buttons().put(new byte[15]).flip();
    raw.buttons().put(0, (byte) 1); // A
    raw.buttons().put(7, (byte) 1); // start
    raw.buttons().put(8, (byte) 1); // the guide button, which this extension leaves out
    raw.axes().put(new float[6]).flip();
    raw.axes().put(GLFW_LEFT_X, 0.5f);
    raw.axes().put(GLFW_LEFT_Y, -0.8f); // up in GLFW, so up on the stage
    raw.axes().put(GLFW_RIGHT_Y, 0.1f); // inside the dead zone
    raw.axes().put(GLFW_RIGHT_TRIGGER, 1.0f);

    GamepadState into = new GamepadState();
    GlfwGamepadSource.copy(raw, into, 1);

    assertTrue(into.pressed(1, GamepadButton.A));
    assertTrue(into.pressed(1, GamepadButton.START));
    assertEquals(0.5, into.stickX(1, GamepadStick.LEFT), 1e-6);
    assertEquals(0.8, into.stickY(1, GamepadStick.LEFT), 1e-6);
    assertEquals(0, into.stickY(1, GamepadStick.RIGHT), 1e-9);
    assertEquals(0, into.stickX(1, GamepadStick.RIGHT), 1e-9);
    assertEquals(0.5, into.trigger(1, GamepadTrigger.LEFT), 1e-6); // −1 → 0
    assertEquals(1, into.trigger(1, GamepadTrigger.RIGHT), 1e-9);
    // no button of the extension answers to GLFW's guide button
    for (GamepadButton b : GamepadButton.values()) {
      if (b == GamepadButton.A || b == GamepadButton.START) {
        continue;
      }
      assertFalse(into.pressed(1, b), b.toString());
    }
  }

  private static final int GLFW_LEFT_X = 0;
  private static final int GLFW_LEFT_Y = 1;
  private static final int GLFW_RIGHT_Y = 3;
  private static final int GLFW_RIGHT_TRIGGER = 5;
}
