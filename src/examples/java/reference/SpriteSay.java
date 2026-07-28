package reference;
import org.openpatch.scratch.*;

public class SpriteSay {
  public SpriteSay() {
    Stage myStage = new Stage(600, 240);
    Sprite zeta = new Sprite("green", "slimeGreen");
    myStage.add(zeta);
    zeta.say("Hi! I'm Zeta and can say line breaks");
    myStage.wait(3000);
  }

  public static void main(String[] args) {
    new SpriteSay();
  }
}
