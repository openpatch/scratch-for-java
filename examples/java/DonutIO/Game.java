import org.openpatch.scratch.Window;

public class Game extends Window {

  public static int LEVEL = 0;

  public Game() {
    super(800, 600, "assets");

    this.setStage(new StartStage());
  }

  public static void main(String[] args) {
    new Game();
  }
}
