package reference;
import org.openpatch.scratch.*;


public class StageSetDebug {
  public StageSetDebug() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // Debug mode draws the hitboxes and shows whatever debug() is given.
    while (true) {
      myStage.setDebug(true);
      myStage.wait(1500);
      myStage.setDebug(false);
      myStage.wait(1500);
    }
  }

  public static void main(String[] args) {
    new StageSetDebug();
  }
}
