package reference;
import org.openpatch.scratch.*;

public class SpriteSay {
  public SpriteSay() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite zeta = new Sprite("green", "slimeGreen");
    myStage.add(zeta);
    zeta.say("Hi! I'm Zeta and can say line breaks");
    myStage.waitSeconds(3);
  }

  public static void main(String[] args) {
    new SpriteSay();
  }
}
