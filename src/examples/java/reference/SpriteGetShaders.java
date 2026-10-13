package reference;
import org.openpatch.scratch.*;


public class SpriteGetShaders {
  public SpriteGetShaders() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite colourful = new Sprite("slime", "slimeGreen");
    colourful.setPosition(-100, 0);
    myStage.add(colourful);

    // the same costume, drawn through a shader that takes the colour out
    Sprite grey = new Sprite("slime", "slimeGreen");
    grey.setPosition(100, 0);
    grey.getShaders().add("grayscale", "assets/grayscale.frag", null);
    myStage.add(grey);
  }

  public static void main(String[] args) {
    new SpriteGetShaders();
  }
}
