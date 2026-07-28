package reference;
import org.openpatch.scratch.*;


public class AnimatedSpriteToString {
  public AnimatedSpriteToString() {
    Stage myStage = new Stage(600, 240);
    AnimatedSprite mySprite = new AnimatedSprite();
    mySprite.addAnimation("walk", "bunny1_walk%d", 2);
    mySprite.playAnimation("walk");
    myStage.add(mySprite);
    // Which animation is playing and how far into it, which is worth printing
    // when an animation does not look the way it should.
    System.out.println(mySprite.toString());
  }

  public static void main(String[] args) {
    new AnimatedSpriteToString();
  }
}
