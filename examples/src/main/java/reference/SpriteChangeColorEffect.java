package reference;
import org.openpatch.scratch.*;

public class SpriteChangeColorEffect {
  public SpriteChangeColorEffect() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    // small steps take the slime once round the colour wheel, over and over
    while (true) {
      mySprite.changeColorEffect(5);
      myStage.waitSeconds(0.05);
    }
  }

  public static void main(String[] args) {
    new SpriteChangeColorEffect();
  }
}
