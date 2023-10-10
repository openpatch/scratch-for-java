import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.recorder.GifRecorder;

public class StageFind {
  public StageFind() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new CustomSprite());
    myStage.add(new CustomSprite());
    myStage.add(new Sprite());

    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    myStage.display("Sprites: " + myStage.find(CustomSprite.class).size());
    myStage.wait(2000);
    recorder.stop();
    myStage.exit();
  }

  class CustomSprite extends Sprite {}

  public static void main(String[] args) {
    new StageFind();
  }
}
