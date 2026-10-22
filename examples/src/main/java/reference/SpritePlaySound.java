package reference;
import org.openpatch.scratch.*;


public class SpritePlaySound {
  public SpritePlaySound() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addSound("bump", "impactWood_heavy_001");
    while (true) {
      mySprite.changeY(40);
      mySprite.playSound("bump");
      myStage.waitSeconds(0.5);
      mySprite.changeY(-40);
      myStage.waitSeconds(0.5);
    }
  }

  public static void main(String[] args) {
    new SpritePlaySound();
  }
}
