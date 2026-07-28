package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetPosition {
  public SpriteSetPosition() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    while (true) {
      int x = myStage.pickRandom(-myStage.getWidth() / 2, myStage.getWidth() / 2);
      int y = myStage.pickRandom(-myStage.getHeight() / 2, myStage.getHeight() / 2);

      mySprite.setPosition(x, y);
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new SpriteSetPosition();
  }
}
