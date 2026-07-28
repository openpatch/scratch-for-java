package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteThink {
  public SpriteThink() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "slimeGreen");
    myStage.add(zeta);
    zeta.think("Hi! I'm Zeta and can think line breaks if the line gets too long.");
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new SpriteThink();
  }
}
