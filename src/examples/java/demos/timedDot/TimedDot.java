package demos.timedDot;

import org.openpatch.scratch.*;

public class TimedDot extends Stage {

  // begin stage designer
  private DotSprite dotSprite;
  // end stage designer

  public TimedDot() {
    // begin stage designer
    dotSprite = new DotSprite();
    this.add(dotSprite);
    // end stage designer
  }

  public void run() {
    if (this.getTimer().everyMillis(2400)) {
      this.eraseAll();
    }
  }

  public static void main(String[] args) {
    Window myWindow = new Window(400, 200);
    myWindow.setStage(new TimedDot());
  }
}

class DotSprite extends Sprite {
  public DotSprite() {
    super();
    this.getPen().setSize(40);
    this.setDirection(65);
  }

  public void run() {
    this.ifOnEdgeBounce();
    if (this.getTimer("timer2").everyMillis(600)) {
      this.getPen().setColor(200);
      this.move(20);
      this.getPen().down();
      this.getPen().up();
    }
    if (this.getTimer("timer1").everyMillis(1200)) {
      this.getPen().setColor(100);
      this.move(20);
      this.getPen().down();
      this.getPen().up();
    }
  }
}
