package reference;
import org.openpatch.scratch.*;


public class ShadersNext {
  public ShadersNext() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.getShaders().add("grayscale", "assets/grayscale.frag");
    mySprite.getShaders().add("invert", "assets/invert.frag");

    while (true) {
      mySprite.say(mySprite.getShaders().getCurrentName());
      myStage.waitSeconds(1);
      // after the last shader comes the first one again
      mySprite.getShaders().next();
    }
  }

  public static void main(String[] args) {
    new ShadersNext();
  }
}
