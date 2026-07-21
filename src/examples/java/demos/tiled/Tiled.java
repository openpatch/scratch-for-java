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
    Text.DEFAULT_FONT = "demos/tiled/assets/Retro Gaming.ttf";
    Text.DEFAULT_FONT_SIZE = 11;
    Text.FONT_SIZES = new int[] { 11 };
    Text.SPEAK_BUBBLE_MAX_LIMIT = 200;
    Text.SMOOTHING = false;

    // Start the Game
    Window.useFullScreen();
    new Tiled();
  }
}
