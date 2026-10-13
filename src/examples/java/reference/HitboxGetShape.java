package reference;
import org.openpatch.scratch.*;


public class HitboxGetShape {
  public HitboxGetShape() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
