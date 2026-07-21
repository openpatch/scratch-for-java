package demos.tiled;

import org.openpatch.scratch.TextureSampling;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.Text;

public class Tiled extends Window {

  public Tiled() {
    super(640, 360, "demos/tiled/assets");

    I18n.setup();

    GameState.load();
    I18n.select(GameState.get().locale);

    this.setStage(new World(GameState.get().map, null));
  }

  public static void main(String[] args) {
    // These global settings must be set before starting the game.
    Window.useTextureSampling(TextureSampling.POINT);
    Text.useFont("demos/tiled/assets/Retro Gaming.ttf", 11);
    Text.useSmoothing(false);
    Text.SPEAK_BUBBLE_MAX_LIMIT = 200;

    // Start the Game
    Window.useFullScreen();
    new Tiled();
  }
}
