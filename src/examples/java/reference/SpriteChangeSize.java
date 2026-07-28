package reference;
import org.openpatch.scratch.*;


public class SpriteChangeSize {
  public SpriteChangeSize() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // The size is a percentage, so 100 is the costume at its own size.
    while (true) {
      while (mySprite.getSize() < 200) {
        mySprite.changeSize(5);
        myStage.wait(50);
      }
      while (mySprite.getSize() > 50) {
        mySprite.changeSize(-5);
        myStage.wait(50);
      }
    }
  }

  public static void main(String[] args) {
    new SpriteChangeSize();
  }
}
