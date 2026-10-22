package reference;
import org.openpatch.scratch.*;


public class StageDebug {
  public StageDebug() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Window.getInstance().setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // Values written onto the stage while it runs, instead of into the console.
    while (true) {
      mySprite.changeX(2);
      myStage.debug("x", mySprite.getX());
      myStage.waitSeconds(0.05);
    }
  }

  public static void main(String[] args) {
    new StageDebug();
  }
}
