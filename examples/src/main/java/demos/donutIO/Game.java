package demos.donutIO;

import org.openpatch.scratch.Window;
import org.openpatch.scratch.Text;

public class Game extends Window {

  public static int LEVEL = 0;

  public Game() {
    super(800, 600, "demos/donutIO/assets");

    // begin project settings
    this.setStage(new StartStage());
    // end project settings
  }

  public static void main(String[] args) {
    // begin project settings
    // end project settings
    new Game();
  }
}
