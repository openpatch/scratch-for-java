package reference;
import org.openpatch.scratch.*;

public class StageIsKeyPressed {
  public StageIsKeyPressed() {
    Stage myStage = new Stage(600, 240);
    while (true) {
      myStage.display("Space pressed? " + myStage.isKeyPressed(KeyCode.SPACE));
    }
  }

  public static void main(String[] args) {
    new StageIsKeyPressed();
  }
}
