package reference;
import org.openpatch.scratch.*;


public class SpriteAsk {
  public SpriteAsk() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // The question appears with a box to type into. getAnswer() has what was
    // typed once isAsking() has gone back to false.
    mySprite.ask("What is your name?");
    while (mySprite.isAsking()) {
      myStage.waitSeconds(0.05);
    }
    mySprite.say("Hello " + mySprite.getAnswer());
  }

  public static void main(String[] args) {
    new SpriteAsk();
  }
}
