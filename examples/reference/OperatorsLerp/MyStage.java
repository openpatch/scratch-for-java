package OperatorsLerp;

import org.openpatch.scratch.Operators;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.pen.Pen;
import org.openpatch.scratch.extensions.timer.Timer;

public class MyStage extends Stage {
  private Pen myPen;
  private int startMillis = 0;

  public MyStage() {
    startMillis = Timer.millis();
    myPen = new Pen();
    this.add(myPen);
  }

  public void run() {
    var currentMillis = Timer.millis();
    var x = Operators.lerp(0, this.getWidth() / 2.0, (currentMillis - startMillis) / 3000.0);
    var y = Operators.lerp(0, this.getHeight() / 2.0, (currentMillis - startMillis) / 3000.0);
    myPen.setPosition(x, y);
    System.out.println(x);
    System.out.println(this.getWidth());
    myPen.down();
  }
}
