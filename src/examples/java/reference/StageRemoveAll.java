package reference;
import org.openpatch.scratch.*;

public class StageRemoveAll {
  public StageRemoveAll() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.changeX(20);
    myStage.add(gamma);
    Sprite zeta = new Sprite("zeta", "slimeGreen");
    zeta.changeX(-20);
    myStage.add(zeta);
    myStage.wait(2000);
    myStage.removeAll();
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageRemoveAll();
  }
}
