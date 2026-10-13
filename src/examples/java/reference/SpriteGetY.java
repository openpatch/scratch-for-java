package reference;
import org.openpatch.scratch.*;

public class SpriteGetY {
  public SpriteGetY() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    mySprite.say("Y: " + mySprite.getY());
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteGetY();
  }
}
