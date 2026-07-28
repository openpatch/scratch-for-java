package reference;
import org.openpatch.scratch.*;

public class StageGetCurrentBackdropName {
  public StageGetCurrentBackdropName() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "background");
    myStage.addBackdrop("sea", "UIbg");
    myStage.display("Name: " + myStage.getCurrentBackdropName());
    myStage.wait(2000);
    myStage.nextBackdrop();
    myStage.display("Name: " + myStage.getCurrentBackdropName());
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageGetCurrentBackdropName();
  }
}
