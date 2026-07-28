package reference;
import org.openpatch.scratch.*;


public class SpriteGlide {
  public SpriteGlide() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Sliding to a place over a set time, instead of jumping there.
    while (true) {
      mySprite.glide(2, 200, 60);
      myStage.wait(2000);
      mySprite.glide(2, -200, -60);
      myStage.wait(2000);
    }
  }

  public static void main(String[] args) {
    new SpriteGlide();
  }
}
