package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteStamp {
  public SpriteStamp() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    mySprite.setX(-80);
    myStage.wait(3000);
    mySprite.stamp();
    mySprite.changeX(50);
    myStage.wait(3000);
    mySprite.stamp();
    mySprite.changeX(50);
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteStamp();
  }
}
