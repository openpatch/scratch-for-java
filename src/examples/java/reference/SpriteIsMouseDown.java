package reference;
import org.openpatch.scratch.*;

public class SpriteIsMouseDown {
  public SpriteIsMouseDown() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.changeX(-80);
    mySprite.changeY(30);
    myStage.add(mySprite);
    while (true) {
      mySprite.say("Mouse down? " + mySprite.isMouseDown());
    }
  }

  public static void main(String[] args) {
    new SpriteIsMouseDown();
  }
}
