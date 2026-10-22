package reference;
import org.openpatch.scratch.*;

public class SpriteChangeY {
  public SpriteChangeY() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    myStage.waitSeconds(1);
    mySprite.changeY(-20);
    myStage.waitSeconds(1);
    mySprite.changeY(40);
    myStage.waitSeconds(1);
  }

  public static void main(String[] args) {
    new SpriteChangeY();
  }
}
