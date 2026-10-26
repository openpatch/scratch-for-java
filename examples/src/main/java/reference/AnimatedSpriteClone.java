package reference;
import org.openpatch.scratch.*;


public class AnimatedSpriteClone {
  public AnimatedSpriteClone() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite();
    mySprite.addAnimation("walk", "bunny1_walk%d", 2);
    mySprite.playAnimation("walk");
    myStage.add(mySprite);

    // A copy with the same animations, playing the same one.
    while (true) {
      Sprite copy = mySprite.createClone();
      copy.goToRandomPosition();
      myStage.waitSeconds(0.7);
    }
  }

  public static void main(String[] args) {
    new AnimatedSpriteClone();
  }
}
