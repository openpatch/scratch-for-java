import org.openpatch.scratch.*;

public class Referee extends Sprite {
  private boolean green = false;

  public Referee() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("sign");
    // scratch4j:end setup
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
