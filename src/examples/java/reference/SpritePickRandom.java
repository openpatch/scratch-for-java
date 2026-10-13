package reference;
import org.openpatch.scratch.*;

public class SpritePickRandom {
  public SpritePickRandom() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite zeta = new Sprite("green", "slimeGreen");
    myStage.add(zeta);
    while (true) {
      int random = zeta.pickRandom(0, 100);
      zeta.think("Random: " + random);
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new SpritePickRandom();
  }
}
