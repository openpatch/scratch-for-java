package reference;
import org.openpatch.scratch.*;

public class SpriteGetDeltaTime {
  public SpriteGetDeltaTime() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);

    while (true) {
      var dt = mySprite.getDeltaTime();
      mySprite.say("Delta Time: " + dt);
    }
  }

  public static void main(String[] args) {
    new SpriteGetDeltaTime();
  }
}
