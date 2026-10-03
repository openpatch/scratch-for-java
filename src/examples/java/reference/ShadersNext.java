package reference;
import org.openpatch.scratch.*;


public class ShadersNext {
  public ShadersNext() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.getShaders().add("grayscale", "assets/grayscale.frag", null);
    mySprite.getShaders().add("invert", "assets/invert.frag", null);

    while (true) {
      mySprite.say(mySprite.getShaders().getCurrentName());
      myStage.wait(1000);
      // after the last shader comes the first one again
      mySprite.getShaders().next();
    }
  }

  public static void main(String[] args) {
    new ShadersNext();
  }
}
