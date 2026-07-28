package reference;
import org.openpatch.scratch.*;


public class SpriteSetVolume {
  public SpriteSetVolume() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addSound("music", "jingles_STEEL16");
    // Half as loud. The volume belongs to the sprite, not to the sound, so it
    // holds for everything this sprite plays.
    mySprite.setVolume(50);
    mySprite.playSound("music");
  }

  public static void main(String[] args) {
    new SpriteSetVolume();
  }
}
