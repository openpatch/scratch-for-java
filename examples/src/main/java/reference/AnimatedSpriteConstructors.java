package reference;
import org.openpatch.scratch.*;


public class AnimatedSpriteConstructors {
  public AnimatedSpriteConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    // An animated sprite is a sprite whose costumes are grouped into named
    // animations, each played frame by frame.
    Sprite mySprite = new Sprite();
    mySprite.addAnimation("walk", "bunny1_walk%d", 2);
    mySprite.playAnimation("walk");
    myStage.add(mySprite);
  }

  public static void main(String[] args) {
    new AnimatedSpriteConstructors();
  }
}
