package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.GifRecorder;
import org.openpatch.scratch.extensions.timer.Timer;

public class WindowSetStage {
  public WindowSetStage() {
    Window myWindow = new Window(500, 200);

    Stage firstLevel = new Stage();
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    firstLevel.add(gamma);

    Stage secondLevel = new Stage();
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    secondLevel.add(zeta);

    Timer switchTimer = new Timer();
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();

    while (switchTimer.forMillis(2000)) {
      myWindow.setStage(secondLevel);
    }

    switchTimer.reset();

    while (switchTimer.forMillis(2000)) {
      myWindow.setStage(firstLevel);
    }
    recorder.stop();

    myWindow.exit();
  }

  public static void main(String[] args) {
    new WindowSetStage();
  }
}
