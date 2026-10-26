package reference;
import org.openpatch.scratch.*;


public class SpriteDebug {
  public SpriteDebug() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    Window.getInstance().setDebug(true);
    // Values printed onto the stage next to the sprite, without stopping it.
    while (true) {
      mySprite.changeX(2);
      mySprite.debug("x", mySprite.getX(), "direction", mySprite.getDirection());
      myStage.waitSeconds(0.05);
    }
  }

  public static void main(String[] args) {
    new SpriteDebug();
  }
}
