package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class SpriteThink {
  public SpriteThink() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite zeta = new Sprite("green", "slimeGreen");
    myStage.add(zeta);
    zeta.think("Hi! I'm Zeta and can think line breaks if the line gets too long.");
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteThink();
  }
}
