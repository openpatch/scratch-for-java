package Tiled;

import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.text.Text;

public class Tiled extends Window {

  public Tiled() {
    super(true, 640, 360, "Tiled/assets");

    I18n.setup();

    GameState.load();
    I18n.select(GameState.get().locale);

    this.setStage(new World(GameState.get().map, null));
  }

  public static void main(String[] args) {
    // These global settings must be set before starting the game.
    Window.TEXTURE_SAMPLING_MODE = 2;
    Text.DEFAULT_FONT = "Tiled/assets/Retro Gaming.ttf";
    Text.DEFAULT_FONT_SIZE = 11;
    Text.FONT_SIZES = new int[] {11};
    Text.SPEAK_BUBBLE_MAX_LIMIT = 200;
    Text.SMOOTHING = false;

    // Start the Game
    new Tiled();
  }
}
