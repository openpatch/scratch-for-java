import org.openpatch.scratch.*;

public class Referee extends Sprite {
  private boolean green = false;

  public Referee() {
    // begin sprite editor
    this.addCostume("sign");
    // end sprite editor
    this.setSize(45);
    this.setPosition(-250, 110);
  }

  public void run() {
    if (this.getTimer().every(2.2)) {
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
