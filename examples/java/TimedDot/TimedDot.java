package TimedDot;

import org.openpatch.scratch.*;

public class TimedDot extends Stage {
  public TimedDot() {
    super(400, 200);
    this.add(new DotSprite());
  }

  public void run() {
    if (this.getTimer().everyMillis(2400)) {
      this.eraseAll();
    }
  }

  public static void main(String[] args) {
    new TimedDot();
  }
}

class DotSprite extends Sprite {
  public DotSprite() {
    super();
    this.addTimer("timer2");
    this.addTimer("timer1");
    this.getPen().setSize(20);
    this.setOnEdgeBounce(true);
    this.setDirection(65);
  }

  public void run() {
    if (this.getTimer("timer2").everyMillis(600)) {
      this.getPen().down();
      this.getPen().setColor(200);
      this.move(20);
      this.getPen().up();
    }
    if (this.getTimer("timer1").everyMillis(1200)) {
      this.getPen().down();
      this.getPen().setColor(100);
      this.move(20);
      this.getPen().up();
    }
  }
}
