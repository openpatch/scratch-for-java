package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteWhenMouseMoved {
  class CustomSprite extends Sprite {
    public CustomSprite() {
      this.addCostume("zeta", "slimeGreen");
      this.addCostume("gamma", "slimePurple");
    }

    @Override
    public void whenMouseMoved(double x, double y) {
      this.setPosition(x, y);
    }
  }

  public SpriteWhenMouseMoved() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new CustomSprite());
    while (true) {}
  }

  public static void main(String[] args) {
    new SpriteWhenMouseMoved();
  }
}
