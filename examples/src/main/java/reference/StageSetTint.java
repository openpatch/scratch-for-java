package reference;
import org.openpatch.scratch.*;

public class StageSetTint {
  public StageSetTint() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    myStage.waitSeconds(2);
    // multiplies every pixel of the backdrop by the colour (white would leave it as it is)
    myStage.setTint(new Color(255, 160, 60));
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new StageSetTint();
  }
}
