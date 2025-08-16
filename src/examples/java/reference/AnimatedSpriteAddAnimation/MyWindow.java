package reference.AnimatedSpriteAddAnimation;

import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.recorder.GifRecorder;
import org.openpatch.scratch.extensions.recorder.Recorder;

public class MyWindow extends Window {
  public MyWindow() {
    Stage myStage = new MyStage();
    this.setStage(myStage);
    Recorder recorder = new GifRecorder("examples/reference/" + this.getClass().getPackageName());
    recorder.start();
    // Wait for 5 seconds
    while (myStage.getTimer().forMillis(5000))
      ;
    recorder.stop();
    this.exit();
  }

  public static void main(String[] args) {
    new MyWindow();
  }
}
