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

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    MySprite mySprite = new MySprite();
    myStage.add(mySprite);
    myStage.waitSeconds(2);
    mySprite.remove();
  }

  public static void main(String[] args) {
    new SpriteWhenRemovedFromStage();
  }
}
