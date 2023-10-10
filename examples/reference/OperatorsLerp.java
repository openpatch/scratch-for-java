import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.pen.*;
import org.openpatch.scratch.extensions.recorder.*;
import org.openpatch.scratch.extensions.timer.*;

public class OperatorsLerp {
  public OperatorsLerp() {
    Stage myStage = new Stage();
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.down();

    int startMillis = Timer.millis();

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    for (int currentMillis = Timer.millis();
        currentMillis - startMillis < 3000;
        currentMillis = Timer.millis()) {
      double x =
          Operators.lerp(
              0, Window.getInstance().getWidth(), (currentMillis - startMillis) / 3000.0);
      double y =
          Operators.lerp(
              0, Window.getInstance().getHeight(), (currentMillis - startMillis) / 3000.0);
      myPen.setPosition(x, y);
    }
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new OperatorsLerp();
  }
}
