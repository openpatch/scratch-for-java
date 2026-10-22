package reference;
import org.openpatch.scratch.*;


public class AnimatedSpriteToString {
  public AnimatedSpriteToString() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite();
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
