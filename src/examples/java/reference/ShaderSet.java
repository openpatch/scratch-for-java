package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.shader.*;


public class ShaderSet {
  public ShaderSet() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    mySprite.setSize(200);
    myStage.add(mySprite);

    Shader pixelate = mySprite.getShaders().add("pixelate", "assets/pixelate.frag", null);

    while (true) {
      // "blocks" is a uniform of pixelate.frag: the further right the mouse,
      // the more blocks across and the sharper the slime
      double blocks = 2 + (myStage.getMouseX() + 300) / 10;
      pixelate.set("blocks", blocks);
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new ShaderSet();
  }
}
