package reference;
import org.openpatch.scratch.*;

public class SpriteGetCurrentCostumeIndex {
  public SpriteGetCurrentCostumeIndex() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
