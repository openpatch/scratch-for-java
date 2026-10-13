package reference;
import org.openpatch.scratch.*;


public class SpriteChangeVolume {
  public SpriteChangeVolume() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addSound("music", "jingles_STEEL16");
    mySprite.playSound("music");
    // Fading out.
    while (mySprite.getVolume() > 0) {
      mySprite.changeVolume(-5);
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new SpriteChangeVolume();
  }
}
