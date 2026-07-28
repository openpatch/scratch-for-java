package reference;
import org.openpatch.scratch.*;


public class SpriteGetMouseX {
  public SpriteGetMouseX() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    while (true) {
      mySprite.say("mouse x: " + mySprite.getMouseX());
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new SpriteGetMouseX();
  }
}
