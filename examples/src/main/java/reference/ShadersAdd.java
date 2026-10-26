package reference;
import org.openpatch.scratch.*;


public class ShadersAdd {
  public ShadersAdd() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // a name for the shader and its fragment shader file
    mySprite.getShaders().add("invert", "assets/invert.frag");
  }

  public static void main(String[] args) {
    new ShadersAdd();
  }
}
