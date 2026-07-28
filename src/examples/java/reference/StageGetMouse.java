package reference;
import org.openpatch.scratch.*;

public class StageGetMouse {
  public StageGetMouse() {
    Stage myStage = new Stage(600, 240);
    while (true) {
      var mouseX = myStage.getMouseX();
      var mouseY = myStage.getMouseY();

      myStage.display("X: " + mouseX + " Y: " + mouseY);
    }
  }

  public static void main(String[] args) {
    new StageGetMouse();
  }
}
