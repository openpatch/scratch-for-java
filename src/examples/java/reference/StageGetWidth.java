package reference;
import org.openpatch.scratch.*;

public class StageGetWidth {
  public StageGetWidth() {
    Stage myStage = new Stage(600, 240);
    myStage.display("Width: " + myStage.getWidth());
    myStage.wait(500);
    myStage.wait(500);
  }

  public static void main(String[] args) {
    new StageGetWidth();
  }
}
