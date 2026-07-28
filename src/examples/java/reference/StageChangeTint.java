package reference;
import org.openpatch.scratch.*;

public class StageChangeTint {
  public StageChangeTint() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "background");
    myStage.wait(2000);
    myStage.changeTint(200);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageChangeTint();
  }
}
