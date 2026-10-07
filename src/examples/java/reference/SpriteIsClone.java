package reference;
import org.openpatch.scratch.*;

public class SpriteIsClone {
  public SpriteIsClone() {
    class Slime extends Sprite {
      public Slime() {
        this.addCostume("slime", "slimeGreen");
      }

      public void whenStartsAsClone() {
        this.setX(150);
      }

      public void run() {
        // The original says so, the clone too.
        this.say(this.isClone() ? "I am a clone" : "I am the original");
      }
    }

    Stage myStage = new Stage(600, 240);
    Slime slime = new Slime();
    myStage.add(slime);
    slime.clone();
  }

  public static void main(String[] args) {
    new SpriteIsClone();
  }
}
