package reference;
import org.openpatch.scratch.*;

public class StageSetTransparency {
  public StageSetTransparency() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    myStage.wait(2000);
    // 0 is fully solid and 100 is invisible, so 50 fades the backdrop halfway
    // into the stage colour behind it.
    myStage.setTransparency(50);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageSetTransparency();
  }
}
