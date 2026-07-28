package reference;
import org.openpatch.scratch.*;

public class SpriteGetCurrentCostumeName {
  public SpriteGetCurrentCostumeName() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.addCostume("gamma", "slimePurple");
    myStage.add(mySprite);

    mySprite.think("Name: " + mySprite.getCurrentCostumeName());
    myStage.wait(2000);
    mySprite.nextCostume();
    mySprite.think("Name: " + mySprite.getCurrentCostumeName());
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteGetCurrentCostumeName();
  }
}
