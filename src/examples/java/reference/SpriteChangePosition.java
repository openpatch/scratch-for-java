package reference;
import org.openpatch.scratch.*;


public class SpriteChangePosition {
  public SpriteChangePosition() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Moving by an amount rather than to a place - either as two numbers or,
    // when the step is already worked out somewhere else, as a vector.
    Vector2 step = new Vector2(10, 5);
    while (true) {
      mySprite.changePosition(step);
      mySprite.ifOnEdgeBounce();
      myStage.wait(50);
      mySprite.changePosition(10, 5);
      mySprite.ifOnEdgeBounce();
      myStage.wait(50);
    }
  }

  public static void main(String[] args) {
    new SpriteChangePosition();
  }
}
