package reference;
import org.openpatch.scratch.*;

public class SpriteHideVariable {
  public SpriteHideVariable() {
    class Slime extends Sprite {
      int steps = 0;

      public Slime() {
        this.addCostume("slime", "slimeGreen");
        this.showVariable("steps", () -> steps);
      }

      public void run() {
        steps++;
        // Only shown while the slime is clicked.
        if (this.isTouchingMousePointer() && this.isMouseDown()) {
          this.showVariable("steps", () -> steps);
        } else {
          this.hideVariable("steps");
        }
      }
    }

    Stage myStage = new Stage(600, 240);
    myStage.add(new Slime());
  }

  public static void main(String[] args) {
    new SpriteHideVariable();
  }
}
