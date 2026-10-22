package reference;
import org.openpatch.scratch.*;


public class SpriteGetMouseX {
  public SpriteGetMouseX() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    while (true) {
      mySprite.say("mouse x: " + mySprite.getMouseX());
      myStage.waitSeconds(0.1);
    }
  }

  public static void main(String[] args) {
    new SpriteGetMouseX();
  }
}
