package reference;
import org.openpatch.scratch.*;


public class StageGetMouseY {
  public StageGetMouseY() {
    Stage myStage = new Stage(600, 240);

    while (true) {
      myStage.display("mouse y: " + myStage.getMouseY());
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new StageGetMouseY();
  }
}
