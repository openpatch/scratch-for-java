package reference;
import org.openpatch.scratch.*;


public class SpritePointTowardsSprite {
  public SpritePointTowardsSprite() {
    Stage myStage = new Stage(600, 240);
    Sprite green = new Sprite("green", "slimeGreen");
    myStage.add(green);

    Sprite blue = new Sprite("blue", "slimeBlue");
    myStage.add(blue);

    // The green one keeps facing the blue one, wherever it wanders off to.
    while (true) {
      blue.goToRandomPosition();
      green.pointTowardsSprite(blue);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new SpritePointTowardsSprite();
  }
}
