package reference;
import org.openpatch.scratch.*;


public class RandomRandomPosition {
  public RandomRandomPosition() {
    Stage myStage = new Stage(600, 240);

    // A point somewhere on the stage, as a vector.
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    while (true) {
      mySprite.setPosition(Random.randomPosition());
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new RandomRandomPosition();
  }
}
