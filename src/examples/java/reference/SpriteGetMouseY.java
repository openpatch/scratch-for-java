package reference;
import org.openpatch.scratch.*;


public class SpriteGetMouseY {
  public SpriteGetMouseY() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    while (true) {
      mySprite.say("mouse y: " + mySprite.getMouseY());
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new SpriteGetMouseY();
  }
}
