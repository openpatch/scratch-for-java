package demos.donutIO;

import org.openpatch.scratch.Window;
import org.openpatch.scratch.Text;

public class Game extends Window {

  public static int LEVEL = 0;

  public Game() {
    super(800, 600, "demos/donutIO/assets");
    this.setStage(new StartStage());
  }

  public static void main(String[] args) {
    Text.useFontSizes(32, 48);
    new Game();
  }
}
