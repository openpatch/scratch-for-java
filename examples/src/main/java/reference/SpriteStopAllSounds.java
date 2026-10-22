package reference;
import org.openpatch.scratch.*;


public class SpriteStopAllSounds {
  public SpriteStopAllSounds() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addSound("music", "jingles_STEEL16");
    mySprite.addSound("bump", "impactWood_heavy_001");
    mySprite.playSound("music");
    mySprite.playSound("bump");
    myStage.waitSeconds(2);
    // Everything this sprite is playing, at once.
    mySprite.stopAllSounds();
  }

  public static void main(String[] args) {
    new SpriteStopAllSounds();
  }
}
