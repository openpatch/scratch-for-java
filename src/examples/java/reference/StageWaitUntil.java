package reference;
import org.openpatch.scratch.*;


public class StageWaitUntil {
  public StageWaitUntil() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    mySprite.glide(3, 200, 0);
    // Holds everything up until something becomes true.
    myStage.waitUntil(() -> !mySprite.isGliding());
    mySprite.say("arrived");
  }

  public static void main(String[] args) {
    new StageWaitUntil();
  }
}
