package reference;
import org.openpatch.scratch.*;


public class SpriteIsAsking {
  public SpriteIsAsking() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.ask("Are you still there?");
    // True for as long as the box is on the stage waiting to be filled in.
    while (mySprite.isAsking()) {
      myStage.waitSeconds(0.05);
    }
    mySprite.say("Thank you");
  }

  public static void main(String[] args) {
    new SpriteIsAsking();
  }
}
