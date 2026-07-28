package reference;
import org.openpatch.scratch.*;

public class SpriteIfOnEdgeBounce {
  public SpriteIfOnEdgeBounce() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    mySprite.setRotationStyle(RotationStyle.LEFT_RIGHT);
    while (true) {
      mySprite.move(5);
      mySprite.ifOnEdgeBounce();
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new SpriteIfOnEdgeBounce();
  }
}
