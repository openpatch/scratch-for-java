package reference;
import org.openpatch.scratch.*;

public class StageSetColorEffect {
  public StageSetColorEffect() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    while (true) {
      myStage.setColorEffect(0);
      myStage.waitSeconds(1);
      myStage.setColorEffect(100);
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new StageSetColorEffect();
  }
}
