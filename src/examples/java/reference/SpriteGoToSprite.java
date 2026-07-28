package reference;
import org.openpatch.scratch.*;


public class SpriteGoToSprite {
  public SpriteGoToSprite() {
    Stage myStage = new Stage(600, 240);
    Sprite green = new Sprite("green", "slimeGreen");
    green.setPosition(-150, 60);
    myStage.add(green);

    Sprite blue = new Sprite("blue", "slimeBlue");
    myStage.add(blue);

    myStage.wait(1500);
    // Straight to where the other sprite stands.
    blue.goToSprite(green);
  }

  public static void main(String[] args) {
    new SpriteGoToSprite();
  }
}
