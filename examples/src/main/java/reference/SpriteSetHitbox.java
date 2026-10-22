package reference;

import org.openpatch.scratch.*;

public class SpriteSetHitbox {
  public SpriteSetHitbox() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Window.getInstance().setDebug(true);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    myStage.waitSeconds(2);
    // a rectangle in costume pixels, with its origin at the top left
    mySprite.setHitbox(new Rectangle(0, 0, 10, 10));
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new SpriteSetHitbox();
  }
}
