import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class Timer extends Stage {
  public Timer() {
    super(1800, 400);
    this.add(new TimerSprite());
  }

  public static void main(String[] args) {
    new Timer();
  }
}

class TimerSprite extends Sprite {
  int x = -1800;

  public TimerSprite() {
    super();
    this.getPen().setSize(20);

    this.addTimer("every");
    this.addTimer("for");
    this.addTimer("after");
    this.addTimer("interval1");
    this.addTimer("interval2");
    this.addTimer("interval3");
    this.addTimer("interval4");
  }

  public void run() {
    int y = -140;
    this.x += 20;
    if (this.getTimer("every").everyMillis(600)) {
      this.getPen().down();
      this.getPen().setColor(20);
      this.setPosition(this.x, y);
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("for").forMillis(600)) {
      this.getPen().down();
      this.getPen().setColor(60);
      this.setPosition(this.x, y);
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("after").afterMillis(600)) {
      this.getPen().down();
      this.getPen().setColor(100);
      this.setPosition(this.x, y);
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval1").intervalMillis(600)) {
      this.getPen().down();

      this.getPen().setColor(140);
      this.setPosition(this.x, y);
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval2").intervalMillis(600, true)) {
      this.getPen().down();
      this.getPen().setColor(180);
      this.setPosition(this.x, y);
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval3").intervalMillis(600, 300)) {
      this.getPen().down();
      this.getPen().setColor(220);
      this.setPosition(this.x, y);
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval4").intervalMillis(600, 300, true)) {
      this.getPen().down();
      this.getPen().setColor(255);
      this.setPosition(this.x, y);
      this.getPen().up();
    }
  }
}
