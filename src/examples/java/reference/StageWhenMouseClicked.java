package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageWhenMouseClicked {

  class CustomStage extends Stage {

    public CustomStage() {
      super(600, 240);
      GifRecorder recorder = new GifRecorder("examples/reference/StageWhenMouseClicked.gif");
      recorder.start();
      while (this.getTimer().forMillis(3000))
        ;
      recorder.stop();
      this.exit();
    }

    @Override
    public void whenMouseClicked(MouseCode code) {
      this.display("Mouse Button clicked: " + code);
    }
  }

  public StageWhenMouseClicked() {
    new CustomStage();
  }

  public static void main(String[] args) {
    new StageWhenMouseClicked();
  }
}
