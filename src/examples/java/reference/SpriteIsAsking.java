package reference;
import org.openpatch.scratch.*;


public class SpriteIsAsking {
  public SpriteIsAsking() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.ask("Are you still there?");
    // True for as long as the box is on the stage waiting to be filled in.
    while (mySprite.isAsking()) {
      myStage.wait(50);
    }
    mySprite.say("Thank you");
  }

  public static void main(String[] args) {
    new SpriteIsAsking();
  }
}
