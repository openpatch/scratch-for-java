package reference;
import org.openpatch.scratch.*;


public class HitboxTranslateAndRotateAndResize {
  public HitboxTranslateAndRotateAndResize() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    Hitbox myHitbox = new Hitbox(new Rectangle(-20, -10, 40, 20));
    // Puts the shape where a sprite of that position, direction and size would
    // be. The sprite does this for its own hitbox every frame.
    myHitbox.translateAndRotateAndResize(45, 0, 0, 100, 50, 200);
    System.out.println("the moved hitbox sits at " + myHitbox.getBounds().x());
  }

  public static void main(String[] args) {
    new HitboxTranslateAndRotateAndResize();
  }
}
