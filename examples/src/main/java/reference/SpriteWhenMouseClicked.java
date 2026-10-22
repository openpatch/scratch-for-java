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

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.add(new MySprite());
  }

  public static void main(String[] args) {
    new SpriteWhenMouseClicked();
  }
}
