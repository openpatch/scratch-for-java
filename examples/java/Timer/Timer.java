package Timer;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class Timer extends Stage {
  public Timer() {
    super(1800, 360);
    this.add(new TimerSprite());
  }

  public static void main(String[] args) {
    new Timer();
  }
}

class TimerSprite extends Sprite {
  int x = -900;

  public TimerSprite() {
    super();
    this.getPen().setSize(40);

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
      this.getPen().setColor(20); // Orange
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("for").forMillis(600)) {
      this.getPen().setColor(60); // Hellgrün
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("after").afterMillis(600)) {
      this.getPen().setColor(100); // Grün
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval1").intervalMillis(600)) {
      this.getPen().setColor(140); // Hellblau
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval2").intervalMillis(600, true)) {
      this.getPen().setColor(180); // Blau
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval3").intervalMillis(600, 300)) {
      this.getPen().setColor(220); // Pink
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.getTimer("interval4").intervalMillis(600, 300, true)) {
      this.getPen().setColor(255); // Rot
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
  }
}
