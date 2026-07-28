package reference;
import org.openpatch.scratch.*;

public class StageWhenMouseWheelMoved {
  class CustomStage extends Stage {
    public CustomStage() {
      super(600, 240);
    }

    @Override
    public void whenMouseWheelMoved(int steps) {
      this.display("Mouse Wheel Steps: " + steps);
    }
  }

  public StageWhenMouseWheelMoved() {
    new CustomStage();
  }

  public static void main(String[] args) {
    new StageWhenMouseWheelMoved();
  }
}
