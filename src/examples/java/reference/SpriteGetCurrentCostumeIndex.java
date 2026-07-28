package reference;
import org.openpatch.scratch.*;

public class SpriteGetCurrentCostumeIndex {
  public SpriteGetCurrentCostumeIndex() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.addCostume("gamma", "slimePurple");
    mySprite.changeY(20);
    myStage.add(mySprite);
    mySprite.think("Index: " + mySprite.getCurrentCostumeIndex());

    myStage.wait(2000);
    mySprite.nextCostume();
    mySprite.think("Index: " + mySprite.getCurrentCostumeIndex());
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteGetCurrentCostumeIndex();
  }
}
