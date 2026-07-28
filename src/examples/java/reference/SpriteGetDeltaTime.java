package reference;
import org.openpatch.scratch.*;

public class SpriteGetDeltaTime {
  public SpriteGetDeltaTime() {
    Stage myStage = new Stage(600, 240);
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
