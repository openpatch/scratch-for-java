package reference;
import org.openpatch.scratch.*;

public class SpriteGetCurrentCostumeName {
  public SpriteGetCurrentCostumeName() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
