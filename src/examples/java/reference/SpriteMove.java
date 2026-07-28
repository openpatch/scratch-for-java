package reference;
import org.openpatch.scratch.*;

public class SpriteMove {
  public SpriteMove() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "slimePurple");
    myStage.add(gamma);
    gamma.turnLeft(20);
    while (true) {
      gamma.move(5);
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new SpriteMove();
  }
}
