package reference;
import org.openpatch.scratch.*;

public class StageIsMouseDown {
  public StageIsMouseDown() {
    Stage myStage = new Stage(600, 240);
    while (true) {
      myStage.display("Mouse down? " + myStage.isMouseDown());
    }
  }

  public static void main(String[] args) {
    new StageIsMouseDown();
  }
}
