package reference;
import org.openpatch.scratch.*;


public class SpriteGetHitbox {
  public SpriteGetHitbox() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Window.getInstance().setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    Shape myHitbox = mySprite.getHitbox();
    System.out.println("The hitbox is " + myHitbox.getBounds().width() + " pixels wide.");
  }

  public static void main(String[] args) {
    new SpriteGetHitbox();
  }
}
