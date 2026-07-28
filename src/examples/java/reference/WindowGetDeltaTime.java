package reference;
import org.openpatch.scratch.*;


public class WindowGetDeltaTime {
  public WindowGetDeltaTime() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    myWindow.setStage(myStage);
    // The seconds the last frame took. Multiplying a speed by it makes the
    // movement the same on a fast machine and a slow one.
    while (true) {
      mySprite.changeX(100 * myWindow.getDeltaTime());
      mySprite.ifOnEdgeBounce();
      myStage.wait(10);
    }
  }

  public static void main(String[] args) {
    new WindowGetDeltaTime();
  }
}
