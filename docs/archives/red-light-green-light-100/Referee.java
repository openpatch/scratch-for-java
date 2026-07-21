import org.openpatch.scratch.*;

public class Referee extends Sprite {
  private boolean green = false;

  public Referee() {
    this.addCostume("sign");
    this.setSize(45);
    this.setPosition(-250, 110);
  }

  public void run() {
    if (this.getTimer().everyMillis(2200)) {
      this.green = !this.green;
      if (this.green) {
        this.say("Go!");
        this.broadcast("go");
      } else {
        this.say("Stop!");
        this.broadcast("stop");
      }
    }
  }
}
