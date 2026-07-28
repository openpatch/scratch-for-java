package reference;
import org.openpatch.scratch.*;


public class SpriteGetTransparency {
  public SpriteGetTransparency() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // 0 is fully solid and 100 is invisible, so a big number is a faint sprite.
    System.out.println("A new sprite is at " + mySprite.getTransparency() + ".");
    mySprite.setTransparency(90);
    System.out.println("Now it is at " + mySprite.getTransparency() + " - nearly gone.");
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteGetTransparency();
  }
}
