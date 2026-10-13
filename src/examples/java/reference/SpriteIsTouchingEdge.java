package reference;
import org.openpatch.scratch.*;

public class SpriteIsTouchingEdge {
  public SpriteIsTouchingEdge() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    mySprite.changeY(-10);
    mySprite.changeX(-80);
    myStage.add(mySprite);
    while (true) {
      mySprite.say("Is touching edge? " + mySprite.isTouchingEdge());
      mySprite.changeY(10);
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new SpriteIsTouchingEdge();
  }
}
