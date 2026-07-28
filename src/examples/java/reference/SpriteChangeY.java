package reference;
import org.openpatch.scratch.*;

public class SpriteChangeY {
  public SpriteChangeY() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    myStage.wait(1000);
    mySprite.changeY(-20);
    myStage.wait(1000);
    mySprite.changeY(40);
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteChangeY();
  }
}
