package reference;
import org.openpatch.scratch.*;

public class StageSwitchBackdrop {
  public StageSwitchBackdrop() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
