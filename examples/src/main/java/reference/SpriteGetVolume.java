package reference;
import org.openpatch.scratch.*;


public class SpriteGetVolume {
  public SpriteGetVolume() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.setVolume(75);
    System.out.println("The sprite plays at " + mySprite.getVolume() + " percent.");
  }

  public static void main(String[] args) {
    new SpriteGetVolume();
  }
}
