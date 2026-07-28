package reference;
import org.openpatch.scratch.*;


public class RandomRandomX {
  public RandomRandomX() {
    Stage myStage = new Stage(600, 240);

    // randomX() stays inside the stage, whatever its width is.
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    while (true) {
      mySprite.setX(Random.randomX());
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new RandomRandomX();
  }
}
