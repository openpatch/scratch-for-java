package reference;
import org.openpatch.scratch.*;


public class SpriteWhenAddedToStage {
  public SpriteWhenAddedToStage() {
    class MySprite extends Sprite {
      public MySprite() {
        this.addCostume("slime", "slimeGreen");
      }

      // Called once the sprite is on a stage, which is the first moment it can
      // ask the stage anything.
      public void whenAddedToStage() {
        this.setX(this.getStage().getWidth() / 2 - 50);
      }
    }

    Stage myStage = new Stage(600, 240);
    myStage.add(new MySprite());
  }

  public static void main(String[] args) {
    new SpriteWhenAddedToStage();
  }
}
