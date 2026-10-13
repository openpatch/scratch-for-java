package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

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
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.add(new CustomSprite());
    while (true) {}
  }

  public static void main(String[] args) {
    new SpriteWhenMouseMoved();
  }
}
