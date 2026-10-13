package reference;
import org.openpatch.scratch.*;

public class SpriteIsKeyPressed {
  public SpriteIsKeyPressed() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.changeX(-80);
    mySprite.changeY(30);
    myStage.add(mySprite);
    while (true) {
      mySprite.say("Space pressed? " + mySprite.isKeyPressed(KeyCode.SPACE));
    }
  }

  public static void main(String[] args) {
    new SpriteIsKeyPressed();
  }
}
