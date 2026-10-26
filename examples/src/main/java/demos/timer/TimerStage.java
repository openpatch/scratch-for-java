package demos.timer;

import org.openpatch.scratch.Timer;

import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class TimerStage extends Stage {

  // begin stage designer
  private TimerSprite timerSprite;
  // end stage designer

  public TimerStage() {
    // begin stage designer
    timerSprite = new TimerSprite();
    this.add(timerSprite);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(1800, 360);
    myWindow.setStage(new TimerStage());
  }
}

class TimerSprite extends Sprite {
  private final Timer timerAfter = new Timer();
  private final Timer timerEvery = new Timer();
  private final Timer timerFor = new Timer();
  private final Timer timerInterval1 = new Timer();
  private final Timer timerInterval2 = new Timer();
  private final Timer timerInterval3 = new Timer();
  private final Timer timerInterval4 = new Timer();
  int x = -900;

  public TimerSprite() {
    super();
    this.getPen().setSize(40);
  }

  public void run() {
    int y = -140;
    this.x += 20;
    if (this.timerEvery.every(0.6)) {
      this.getPen().setColor(8); // Orange
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.timerFor.during(0.6)) {
      this.getPen().setColor(24); // Hellgrün
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.timerAfter.after(0.6)) {
      this.getPen().setColor(39); // Grün
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.timerInterval1.interval(0.6)) {
      this.getPen().setColor(55); // Hellblau
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (!this.timerInterval2.interval(0.6)) {
      this.getPen().setColor(71); // Blau
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (this.timerInterval3.interval(0.6, 0.3)) {
      this.getPen().setColor(86); // Pink
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
    y += 40;
    if (!this.timerInterval4.interval(0.3, 0.6)) {
      this.getPen().setColor(100); // Rot
      this.setPosition(this.x, y);
      this.getPen().down();
      this.getPen().up();
    }
  }
}
