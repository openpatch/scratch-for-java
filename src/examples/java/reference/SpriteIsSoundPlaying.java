package reference;
import org.openpatch.scratch.*;


public class SpriteIsSoundPlaying {
  public SpriteIsSoundPlaying() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addSound("music", "jingles_STEEL16");
    mySprite.playSound("music");
    while (true) {
      mySprite.say("playing: " + mySprite.isSoundPlaying("music"));
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new SpriteIsSoundPlaying();
  }
}
