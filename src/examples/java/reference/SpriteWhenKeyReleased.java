package reference;
import org.openpatch.scratch.*;


public class SpriteWhenKeyReleased {
  public SpriteWhenKeyReleased() {
    class MySprite extends Sprite {
      public MySprite() {
        this.addCostume("slime", "slimeGreen");
      }

      // Click into the stage first, so that it takes the keyboard.
      public void whenKeyReleased(KeyCode keyCode) {
        if (keyCode == KeyCode.SPACE) {
          this.say("let go of space");
        }
      }
    }

    Stage myStage = new Stage(600, 240);
    myStage.add(new MySprite());
  }

  public static void main(String[] args) {
    new SpriteWhenKeyReleased();
  }
}
