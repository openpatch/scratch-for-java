package reference;
import org.openpatch.scratch.*;


public class ShaderSet {
  public ShaderSet() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    mySprite.setSize(200);
    myStage.add(mySprite);

    Shader pixelate = mySprite.getShaders().add("pixelate", "assets/pixelate.frag");

    while (true) {
      // "blocks" is a uniform of pixelate.frag: the further right the mouse,
      // the more blocks across and the sharper the slime
      double blocks = 2 + (myStage.getMouseX() + 300) / 10;
      pixelate.set("blocks", blocks);
      myStage.waitSeconds(0.02);
    }
  }

  public static void main(String[] args) {
    new ShaderSet();
  }
}
