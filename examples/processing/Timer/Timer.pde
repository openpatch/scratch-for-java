import org.openpatch.scratch.*;

TimerSprite timer;

void setup() {
  frameRate(10);
  size(1800, 400);
  stage = new Stage(this);

  timer = new TimerSprite();
}

void draw() {
  if(Stage.getInstance().getTimer().afterMillis(2400)) {
    timer.draw();
  }
}

class TimerSprite extends Sprite {
  int x = 0;

  TimerSprite() {
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

  void run() {
    int y = 20;
    x += 20;
    if (this.getTimer("every").everyMillis(600)) {
      this.getPen().down();
      this.getPen().setColor(20);
      this.setPosition(x, y);
      this.getPen().up();
    }
    y += 20;
    if (this.getTimer("for").forMillis(600)) {
      this.getPen().down();
      this.getPen().setColor(60);
      this.setPosition(x, y);
      this.getPen().up();
    }
    y += 20;
    if (this.getTimer("after").afterMillis(600)) {
      this.getPen().down();
      this.getPen().setColor(100);
      this.setPosition(x, y);
      this.getPen().up();
    }
    y += 20;
    if (this.getTimer("interval1").intervalMillis(600)) {
      this.getPen().down();

      this.getPen().setColor(140);
      this.setPosition(x, y);
      this.getPen().up();
    }
    y += 20;
    if (this.getTimer("interval2").intervalMillis(600, true)) {
      this.getPen().down();
      this.getPen().setColor(180);
      this.setPosition(x, y);
      this.getPen().up();
    }
    y += 20;
    if (this.getTimer("interval3").intervalMillis(600, 300)) {
      this.getPen().down();
      this.getPen().setColor(220);
      this.setPosition(x, y);
      this.getPen().up();
    }
    y += 20;
    if (this.getTimer("interval4").intervalMillis(600, 300, true)) {
      this.getPen().down();
      this.getPen().setColor(255);
      this.setPosition(x, y);
      this.getPen().up();
    }
  }
}
