package reference;
import org.openpatch.scratch.*;


public class SpriteGetPosition {
  public SpriteGetPosition() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.setPosition(-120, 60);
    Vector2 position = mySprite.getPosition();
    System.out.println("The sprite is at " + position + ".");
    System.out.println("That is " + position.length() + " pixels from the middle.");
  }

  public static void main(String[] args) {
    new SpriteGetPosition();
  }
}
