package reference;
import org.openpatch.scratch.*;


public class SpriteWhenRemovedFromStage {
  public SpriteWhenRemovedFromStage() {
    class MySprite extends Sprite {
      public MySprite() {
        this.addCostume("slime", "slimeGreen");
      }

      public void whenRemovedFromStage() {
        System.out.println("Goodbye");
      }
    }

    Stage myStage = new Stage(600, 240);
    MySprite mySprite = new MySprite();
    myStage.add(mySprite);
    myStage.wait(2000);
    mySprite.remove();
  }

  public static void main(String[] args) {
    new SpriteWhenRemovedFromStage();
  }
}
