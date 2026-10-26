package reference;
import org.openpatch.scratch.*;

public class SpriteMove {
  public SpriteMove() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite gamma = new Sprite("gamma", "slimePurple");
    myStage.add(gamma);
    gamma.turnLeft(20);
    while (true) {
      gamma.move(5);
      myStage.waitSeconds(0.1);
    }
  }

  public static void main(String[] args) {
    new SpriteMove();
  }
}
