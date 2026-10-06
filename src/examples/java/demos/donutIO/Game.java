package demos.donutIO;

import org.openpatch.scratch.Window;
import org.openpatch.scratch.Text;

public class Game extends Window {

  public static int LEVEL = 0;

  public Game() {
    super(800, 600, "demos/donutIO/assets");

    // scratch4j:begin window (managed by the project settings)
    this.setStage(new StartStage());
    // scratch4j:end window
  }

  public static void main(String[] args) {
    Text.useFontSizes(32, 48);
    // scratch4j:begin options (managed by the project settings)
    // scratch4j:end options
    new Game();
  }
}
