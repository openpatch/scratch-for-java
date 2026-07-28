package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteWhenClicked {
  class CustomSprite extends Sprite {
    public CustomSprite() {
      this.addCostume("zeta", "slimeGreen");
      this.addCostume("gamma", "slimePurple");
    }

    @Override
    public void whenClicked() {
      this.nextCostume();
    }
  }

  public SpriteWhenClicked() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new CustomSprite());
  }

  public static void main(String[] args) {
    new SpriteWhenClicked();
  }
}
