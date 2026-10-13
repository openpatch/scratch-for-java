package reference;
import org.openpatch.scratch.*;


public class RandomRandomY {
  public RandomRandomY() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

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
