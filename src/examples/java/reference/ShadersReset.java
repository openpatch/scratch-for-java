package reference;
import org.openpatch.scratch.*;


public class ShadersReset {
  public ShadersReset() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.getShaders().add("invert", "assets/invert.frag", null);

    while (true) {
      myStage.wait(1000);
      // back to the plain costume; the shader is kept for later
      mySprite.getShaders().reset();
      myStage.wait(1000);
      mySprite.getShaders().switchTo("invert");
    }
  }

  public static void main(String[] args) {
    new ShadersReset();
  }
}
