package reference;
import org.openpatch.scratch.*;

public class SpriteShowVariable {
  public SpriteShowVariable() {
    class Slime extends Sprite {
      int bounces = 0;

      public Slime() {
        this.addCostume("slime", "slimeGreen");
        // Shown as "Slime: bounces", like a sprite's variable in Scratch.
        this.showVariable("bounces", () -> bounces);
      }

      public void run() {
        this.move(4);
        if (this.isTouchingEdge()) {
          bounces++;
          this.turnRight(180);
        }
      }
    }

    Stage myStage = new Stage(600, 240);
    myStage.add(new Slime());
  }

  public static void main(String[] args) {
    new SpriteShowVariable();
  }
}
