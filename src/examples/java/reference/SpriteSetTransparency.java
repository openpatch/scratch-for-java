package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetTransparency {
  public SpriteSetTransparency() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    myStage.wait(2000);
    // The ghost effect: 0 is the solid sprite you start with, 100 is invisible.
    mySprite.setTransparency(50);
    myStage.wait(2000);
    mySprite.setTransparency(90);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteSetTransparency();
  }
}
