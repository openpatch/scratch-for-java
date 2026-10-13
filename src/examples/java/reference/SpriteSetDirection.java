package reference;
import org.openpatch.scratch.*;

public class SpriteSetDirection {
  public SpriteSetDirection() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    mySprite.say("Direction: " + mySprite.getDirection());
    myStage.wait(2000);
    mySprite.setDirection(45);
    mySprite.say("Direction: " + mySprite.getDirection());
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteSetDirection();
  }
}
