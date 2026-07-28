package reference;
import org.openpatch.scratch.*;

public class StageSwitchBackdrop {
  public StageSwitchBackdrop() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "background");
    myStage.addBackdrop("sea", "UIbg");
    myStage.wait(2000);
    myStage.switchBackdrop("sea");
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageSwitchBackdrop();
  }
}
