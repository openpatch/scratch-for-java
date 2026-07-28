package reference;
import org.openpatch.scratch.*;


public class HitboxGetShape {
  public HitboxGetShape() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    Hitbox myHitbox = mySprite.getHitbox();
    // The shape the hitbox is made of, in the place the sprite currently is.
    Shape myShape = myHitbox.getShape();
    System.out.println("the sprite covers " + myShape.getBounds().width() + " pixels across");
  }

  public static void main(String[] args) {
    new HitboxGetShape();
  }
}
