package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteStamp {
  public SpriteStamp() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    mySprite.setX(-80);
    myStage.waitSeconds(3);
    mySprite.stamp();
    mySprite.changeX(50);
    myStage.waitSeconds(3);
    mySprite.stamp();
    mySprite.changeX(50);
    myStage.waitSeconds(3);
  }

  public static void main(String[] args) {
    new SpriteStamp();
  }
}
