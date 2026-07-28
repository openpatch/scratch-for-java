package reference;
import org.openpatch.scratch.*;


public class SpriteGetHitbox {
  public SpriteGetHitbox() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    Hitbox myHitbox = mySprite.getHitbox();
    System.out.println("The hitbox is " + myHitbox.getBounds().width() + " pixels wide.");
  }

  public static void main(String[] args) {
    new SpriteGetHitbox();
  }
}
