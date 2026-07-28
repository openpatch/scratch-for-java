package reference;
import org.openpatch.scratch.*;


public class SpriteStopSound {
  public SpriteStopSound() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addSound("music", "jingles_STEEL16");
    mySprite.playSound("music");
    myStage.wait(2000);
    mySprite.stopSound("music");
  }

  public static void main(String[] args) {
    new SpriteStopSound();
  }
}
