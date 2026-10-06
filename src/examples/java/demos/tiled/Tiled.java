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

    // scratch4j:begin window (managed by the project settings)
    // scratch4j:end window
  }

  public static void main(String[] args) {
    // These global settings must be set before starting the game.
    Text.useFont("demos/tiled/assets/Retro Gaming.ttf", 11);
    Text.useSmoothing(false);
    Text.SPEAK_BUBBLE_MAX_LIMIT = 200;

    // Start the Game
    // scratch4j:begin options (managed by the project settings)
    Window.useTextureSampling(TextureSampling.POINT);
    Window.useFullScreen();
    // scratch4j:end options
    new Tiled();
  }
}
