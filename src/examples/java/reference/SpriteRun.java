package reference;
import org.openpatch.scratch.*;

public class SpriteRun {
  public SpriteRun() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
