package DonutIO;

import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.text.Text;

public class Game extends Window {

  public static int LEVEL = 0;

  public Game() {
    super(800, 600, "DonutIO/assets");

    this.setStage(new StartStage());
  }

  public static void main(String[] args) {
    Text.FONT_SIZES = new int[] {32, 48};
    new Game();
  }
}
