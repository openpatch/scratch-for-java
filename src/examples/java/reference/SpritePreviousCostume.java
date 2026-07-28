package reference;
import org.openpatch.scratch.*;


public class SpritePreviousCostume {
  public SpritePreviousCostume() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addCostume("blue", "slimeBlue");
    mySprite.addCostume("purple", "slimePurple");
    // Backwards through the costumes, wrapping round to the last one.
    while (true) {
      mySprite.previousCostume();
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new SpritePreviousCostume();
  }
}
