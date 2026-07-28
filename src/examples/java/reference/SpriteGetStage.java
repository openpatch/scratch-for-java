package reference;
import org.openpatch.scratch.*;


public class SpriteGetStage {
  public SpriteGetStage() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // A sprite knows the stage it was added to.
    Stage stage = mySprite.getStage();
    System.out.println("The stage is " + stage.getWidth() + " by " + stage.getHeight() + ".");
  }

  public static void main(String[] args) {
    new SpriteGetStage();
  }
}
