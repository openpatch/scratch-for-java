package reference;
import org.openpatch.scratch.*;


public class SpriteIsGliding {
  public SpriteIsGliding() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.glide(3, 200, 0);
    while (mySprite.isGliding()) {
      mySprite.say("on my way ...");
      myStage.wait(100);
    }
    mySprite.say("arrived");
  }

  public static void main(String[] args) {
    new SpriteIsGliding();
  }
}
