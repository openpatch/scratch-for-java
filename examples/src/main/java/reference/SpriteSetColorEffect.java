package reference;
import org.openpatch.scratch.*;

public class SpriteSetColorEffect {
  public SpriteSetColorEffect() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeBlue");
    myStage.add(mySprite);
    // 0 is the costume as drawn, 100 is halfway round the colour wheel, 200 is back again
    while (true) {
      mySprite.setColorEffect(0);
      myStage.waitSeconds(1);
      mySprite.setColorEffect(50);
      myStage.waitSeconds(1);
      mySprite.setColorEffect(100);
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new SpriteSetColorEffect();
  }
}
