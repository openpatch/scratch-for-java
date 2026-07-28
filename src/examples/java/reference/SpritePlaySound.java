package reference;
import org.openpatch.scratch.*;


public class SpritePlaySound {
  public SpritePlaySound() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.addSound("bump", "impactWood_heavy_001");
    while (true) {
      mySprite.changeY(40);
      mySprite.playSound("bump");
      myStage.wait(500);
      mySprite.changeY(-40);
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new SpritePlaySound();
  }
}
