package reference;
import org.openpatch.scratch.*;

public class StageSwitchBackdrop {
  public StageSwitchBackdrop() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    myStage.addBackdrop("sea", "UIbg");
    myStage.waitSeconds(2);
    myStage.switchBackdrop("sea");
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new StageSwitchBackdrop();
  }
}
