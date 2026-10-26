package reference;
import org.openpatch.scratch.*;


public class ShadersSwitchTo {
  public ShadersSwitchTo() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // the first shader added is the one drawn with
    mySprite.getShaders().add("grayscale", "assets/grayscale.frag");
    mySprite.getShaders().add("invert", "assets/invert.frag");

    while (true) {
      if (mySprite.isMouseDown()) {
        mySprite.getShaders().switchTo("invert");
      } else {
        mySprite.getShaders().switchTo("grayscale");
      }
      myStage.waitSeconds(0.02);
    }
  }

  public static void main(String[] args) {
    new ShadersSwitchTo();
  }
}
