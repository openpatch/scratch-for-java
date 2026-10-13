package reference;
import org.openpatch.scratch.*;

public class StageGetCurrentBackdropName {
  public StageGetCurrentBackdropName() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
