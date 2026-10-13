package reference;
import org.openpatch.scratch.*;


public class RandomRandomX {
  public RandomRandomX() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

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
