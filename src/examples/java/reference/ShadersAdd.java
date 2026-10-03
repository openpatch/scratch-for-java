package reference;
import org.openpatch.scratch.*;


public class ShadersAdd {
  public ShadersAdd() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // a fragment shader file, and null for Processing's default vertex shader
    mySprite.getShaders().add("invert", "assets/invert.frag", null);
  }

  public static void main(String[] args) {
    new ShadersAdd();
  }
}
