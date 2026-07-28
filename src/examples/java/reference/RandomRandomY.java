package reference;
import org.openpatch.scratch.*;


public class RandomRandomY {
  public RandomRandomY() {
    Stage myStage = new Stage(600, 240);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    while (true) {
      mySprite.setY(Random.randomY());
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new RandomRandomY();
  }
}
