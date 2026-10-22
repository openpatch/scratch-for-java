package reference;
import org.openpatch.scratch.*;

public class StageRemoveAll {
  public StageRemoveAll() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.changeX(20);
    myStage.add(gamma);
    Sprite zeta = new Sprite("zeta", "slimeGreen");
    zeta.changeX(-20);
    myStage.add(zeta);
    myStage.waitSeconds(2);
    myStage.removeAll();
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new StageRemoveAll();
  }
}
