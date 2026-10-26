package reference.OperatorsLerp;

import org.openpatch.scratch.Operators;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Pen;
import org.openpatch.scratch.Timer;

public class MyStage extends Stage {
  private Pen myPen;
  private Timer myTimer = new Timer();

  public MyStage() {
    myPen = new Pen();
    this.add(myPen);
  }

  public void run() {
    var seconds = myTimer.getSeconds();
    var x = Operators.lerp(0, this.getWidth() / 2.0, seconds / 3.0);
    var y = Operators.lerp(0, this.getHeight() / 2.0, seconds / 3.0);
    myPen.setPosition(x, y);
    System.out.println(x);
    System.out.println(this.getWidth());
    myPen.down();
  }
}
