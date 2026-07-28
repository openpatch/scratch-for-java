package reference;
import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetRotationStyle {
  public SpriteSetRotationStyle() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    mySprite.say("Rotation: " + mySprite.getDirection());
    myStage.wait(1000);
    mySprite.setDirection(45);
    mySprite.say("All-Around: " + mySprite.getDirection());
    myStage.wait(1000);
    mySprite.setRotationStyle(RotationStyle.DONT);
    mySprite.setDirection(180);
    mySprite.say("Don't: " + mySprite.getDirection());
    myStage.wait(1000);
    mySprite.setRotationStyle(RotationStyle.LEFT_RIGHT);
    mySprite.setDirection(200);
    mySprite.say("LEFT-RIGHT: " + mySprite.getDirection());
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteSetRotationStyle();
  }
}
