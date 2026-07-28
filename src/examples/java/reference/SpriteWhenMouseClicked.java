package reference;
import org.openpatch.scratch.*;


public class SpriteWhenMouseClicked {
  public SpriteWhenMouseClicked() {
    class MySprite extends Sprite {
      public MySprite() {
        this.addCostume("slime", "slimeGreen");
      }

      // Anywhere on the stage, not only on the sprite - that is whenClicked().
      public void whenMouseClicked(MouseCode mouseCode) {
        if (mouseCode == MouseCode.LEFT) {
          this.goToMousePointer();
        }
      }
    }

    Stage myStage = new Stage(600, 240);
    myStage.add(new MySprite());
  }

  public static void main(String[] args) {
    new SpriteWhenMouseClicked();
  }
}
