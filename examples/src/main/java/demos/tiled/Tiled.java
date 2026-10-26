package demos.tiled;

import org.openpatch.scratch.Window;
import org.openpatch.scratch.Text;

public class Tiled extends Window {

  public Tiled() {
    super(640, 360, "demos/tiled/assets");

    I18n.setup();

    GameState.load();
    I18n.select(GameState.get().locale);

    this.setStage(new Level(GameState.get().map, null));

    // begin project settings
    // end project settings
  }

  public static void main(String[] args) {
    // These global settings must be set before starting the game.
    Text.useFont("demos/tiled/assets/Retro Gaming.ttf");

    // Start the Game
    // begin project settings
    Window.usePixelArt();
    Window.useFullScreen();
    // end project settings
    new Tiled();
  }
}
