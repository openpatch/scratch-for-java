package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetSize {
  public SpriteSetSize() {
    Stage myStage = new Stage(600, 240);
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
