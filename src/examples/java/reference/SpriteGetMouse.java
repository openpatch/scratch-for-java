package reference;
import org.openpatch.scratch.*;

public class SpriteGetMouse {
  public SpriteGetMouse() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.changeY(30);
    mySprite.changeX(-100);
    myStage.add(mySprite);
    while (true) {
      mySprite.say("X: " + mySprite.getMouseX() + " Y: " + mySprite.getMouseY());
    }
  }

  public static void main(String[] args) {
    new SpriteGetMouse();
  }
}
