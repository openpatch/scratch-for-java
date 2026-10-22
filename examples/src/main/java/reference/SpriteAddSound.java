package reference;
import org.openpatch.scratch.*;


public class SpriteAddSound {
  public SpriteAddSound() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // A sound has a name of its own, which is what playSound() asks for later.
    mySprite.addSound("bump", "impactWood_heavy_001");
    while (true) {
      mySprite.playSound("bump");
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new SpriteAddSound();
  }
}
