package reference;
import org.openpatch.scratch.*;


public class SpriteStopSound {
  public SpriteStopSound() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addSound("music", "jingles_STEEL16");
    mySprite.playSound("music");
    myStage.waitSeconds(2);
    mySprite.stopSound("music");
  }

  public static void main(String[] args) {
    new SpriteStopSound();
  }
}
