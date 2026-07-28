package reference;
import org.openpatch.scratch.*;


public class StageWait {
  public StageWait() {
    Stage myStage = new Stage(600, 240);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // Holds everything up for a while, which is how a program written straight
    // down the page paces itself.
    while (true) {
      mySprite.changeX(50);
      myStage.wait(500);
      mySprite.ifOnEdgeBounce();
    }
  }

  public static void main(String[] args) {
    new StageWait();
  }
}
