package reference;
import org.openpatch.scratch.*;

public class StageWhenMouseWheelMoved {
  class CustomStage extends Stage {
    public CustomStage() {
    }

    @Override
    public void whenMouseWheelMoved(int steps) {
      this.display("Mouse Wheel Steps: " + steps);
    }
  }

  public StageWhenMouseWheelMoved() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new CustomStage());
  }

  public static void main(String[] args) {
    new StageWhenMouseWheelMoved();
  }
}
