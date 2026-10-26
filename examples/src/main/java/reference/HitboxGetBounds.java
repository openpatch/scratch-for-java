package reference;
import org.openpatch.scratch.*;


public class HitboxGetBounds {
  public HitboxGetBounds() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Window.getInstance().setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    Bounds myBounds = mySprite.getHitbox().getBounds();
    // The smallest upright rectangle the hitbox fits into.
    System.out.println("x " + myBounds.x() + ", y " + myBounds.y());
    System.out.println("width " + myBounds.width() + ", height " + myBounds.height());
  }

  public static void main(String[] args) {
    new HitboxGetBounds();
  }
}
