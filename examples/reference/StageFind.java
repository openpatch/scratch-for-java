import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;

public class StageFind {
  public StageFind() {
    Stage myStage = new Stage(254, 100);
    myStage.add(new Pen());
    myStage.add(new Pen());
    myStage.add(new Sprite());

    GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.display("Sprites: " + myStage.find(Sprite.class).size());
    myStage.wait(2000);
    myStage.display("Pens: " + myStage.find(Pen.class).size());
    myStage.wait(2000);
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new StageFind();
  }
}
