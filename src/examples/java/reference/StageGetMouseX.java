package reference;
import org.openpatch.scratch.*;


public class StageGetMouseX {
  public StageGetMouseX() {
    Stage myStage = new Stage(600, 240);

    while (true) {
      myStage.display("mouse x: " + myStage.getMouseX());
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new StageGetMouseX();
  }
}
