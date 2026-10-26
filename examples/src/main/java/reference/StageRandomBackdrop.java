package reference;
import org.openpatch.scratch.*;


public class StageRandomBackdrop {
  public StageRandomBackdrop() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.addBackdrop("first", "background");
    myStage.addBackdrop("second", "UIbg");
    while (true) {
      myStage.randomBackdrop();
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new StageRandomBackdrop();
  }
}
