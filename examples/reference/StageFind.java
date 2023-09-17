import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.pen.Pen;
import org.openpatch.scratch.extensions.recorder.GifRecorder;

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
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new StageFind();
  }
}
