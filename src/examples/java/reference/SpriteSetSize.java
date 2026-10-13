package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteSetSize {
  public SpriteSetSize() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    mySprite.changeX(-80);
    mySprite.changeY(30);
    mySprite.say("Size: " + mySprite.getSize());
    myStage.wait(2000);
    mySprite.setSize(50);
    mySprite.say("Size: " + mySprite.getSize());
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new SpriteSetSize();
  }
}
