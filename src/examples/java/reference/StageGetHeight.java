package reference;
import org.openpatch.scratch.*;

public class StageGetHeight {
  public StageGetHeight() {
    Stage myStage = new Stage(600, 240);
    myStage.display("Height: " + myStage.getHeight());
    myStage.wait(500);
  }

  public static void main(String[] args) {
    new StageGetHeight();
  }
}
