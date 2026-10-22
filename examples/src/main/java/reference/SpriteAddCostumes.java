package reference;
import org.openpatch.scratch.*;


public class SpriteAddCostumes {
  public SpriteAddCostumes() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Every tile of a sheet at once. This one cuts the built-in backdrop,
    // which is 800 by 480, into four quarters called part0 to part3.
    mySprite.addCostumes("part", "background", 400, 240);
    mySprite.setSize(30);
    while (true) {
      mySprite.nextCostume();
      myStage.waitSeconds(0.2);
    }
  }

  public static void main(String[] args) {
    new SpriteAddCostumes();
  }
}
