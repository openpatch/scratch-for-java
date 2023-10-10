import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageWhenMouseWheelMoved {

  class CustomStage extends Stage {

    public CustomStage() {
      super(600, 240);
      GifRecorder recorder = new GifRecorder("examples/reference/StageWhenMouseWheelMoved.gif");
      recorder.start();
      while (this.getTimer().forMillis(3000))
        ;
      recorder.stop();
      this.exit();
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
