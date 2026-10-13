package reference;
import org.openpatch.scratch.*;


public class AnimatedSpriteClone {
  public AnimatedSpriteClone() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    AnimatedSprite mySprite = new AnimatedSprite();
    mySprite.addAnimation("walk", "bunny1_walk%d", 2);
    mySprite.playAnimation("walk");
    myStage.add(mySprite);

    // A copy with the same animations, playing the same one.
    while (true) {
      AnimatedSprite copy = mySprite.clone();
      copy.goToRandomPosition();
      myStage.wait(700);
    }
  }

  public static void main(String[] args) {
    new AnimatedSpriteClone();
  }
}
