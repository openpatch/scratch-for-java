package reference;
import org.openpatch.scratch.*;

public class StageChangeColorEffect {
  public StageChangeColorEffect() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    while (true) {
      myStage.changeColorEffect(5);
      myStage.waitSeconds(0.05);
    }
  }

  public static void main(String[] args) {
    new StageChangeColorEffect();
  }
}
