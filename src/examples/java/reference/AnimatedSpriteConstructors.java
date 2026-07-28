package reference;
import org.openpatch.scratch.*;


public class AnimatedSpriteConstructors {
  public AnimatedSpriteConstructors() {
    Stage myStage = new Stage(600, 240);
    // An animated sprite is a sprite whose costumes are grouped into named
    // animations, each played frame by frame.
    AnimatedSprite mySprite = new AnimatedSprite();
    mySprite.addAnimation("walk", "bunny1_walk%d", 2);
    mySprite.playAnimation("walk");
    myStage.add(mySprite);
  }

  public static void main(String[] args) {
    new AnimatedSpriteConstructors();
  }
}
