package reference;
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteWhenKeyPressed {
  class CustomSprite extends Sprite {
    public CustomSprite() {
      this.addCostume("zeta", "slimeGreen");
      this.addCostume("gamma", "slimePurple");
    }

    @Override
    public void whenKeyPressed(KeyCode keyCode) {
      if (keyCode == KeyCode.UP) {
        this.changeY(20);
      } else if (keyCode == KeyCode.DOWN) {
        this.changeY(-20);
      } else if (keyCode == KeyCode.LEFT) {
        this.changeX(-20);
      } else if (keyCode == KeyCode.RIGHT) {
        this.changeX(20);
      }
    }
  }

  public SpriteWhenKeyPressed() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new CustomSprite());
  }

  public static void main(String[] args) {
    new SpriteWhenKeyPressed();
  }
}
