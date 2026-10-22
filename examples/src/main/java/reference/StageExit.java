package reference;
import org.openpatch.scratch.*;

public class StageExit {
  public StageExit() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    myStage.waitSeconds(3);
    // Closes the window and ends the program.
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new StageExit();
  }
}
