package reference;
import org.openpatch.scratch.*;

public class SpriteRun {
  public SpriteRun() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new Zeta());
    while (true) {
      // wait for 3000 millis
    }
  }

  public static void main(String[] args) {
    new SpriteRun();
  }
}

class Zeta extends Sprite {
  public Zeta() {
    super("green", "slimeGreen");
  }

  @Override
  public void run() {
    this.move(5);
  }
}
