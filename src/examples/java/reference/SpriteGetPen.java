package reference;
import org.openpatch.scratch.*;

public class SpriteGetPen {
  public SpriteGetPen() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);
    // Every sprite carries a pen of its own, which draws where the sprite goes.
    mySprite.setX(-250);
    mySprite.getPen().down();
    while (mySprite.getX() < 250) {
      mySprite.changeX(5);
      myStage.wait(50);
    }
    // Lifting it lets the sprite move on without drawing.
    mySprite.getPen().up();
  }

  public static void main(String[] args) {
    new SpriteGetPen();
  }
}
