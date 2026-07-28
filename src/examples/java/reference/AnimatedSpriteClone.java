package reference;
import org.openpatch.scratch.*;


public class AnimatedSpriteClone {
  public AnimatedSpriteClone() {
    Stage myStage = new Stage(600, 240);
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
