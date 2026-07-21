import org.openpatch.scratch.*;

public class Racer extends Sprite {
  private String creature;
  private double speed;
  private boolean running = false;

  public Racer(String creature, double y, double speed) {
    this.creature = creature;
    this.speed = speed;
    this.addCostume(creature);
    this.addCostume(creature + "_move");
    this.setSize(45);
    this.setPosition(-260, y);
  }

  public void whenIReceive(String message) {
    if (message.equals("go")) {
      this.running = true;
    }
    if (message.equals("stop")) {
      this.running = false;
      this.switchCostume(this.creature);
    }
  }

  public void run() {
    if (!this.running) {
      return;
    }
    this.changeX(this.speed);
    if (this.getTimer().everyMillis(150)) {
      this.nextCostume();
    }
    if (this.getX() > 170) {
      this.say("I win!");
      this.running = false;
    }
  }
}
