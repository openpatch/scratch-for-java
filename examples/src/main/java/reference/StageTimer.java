package reference;
import org.openpatch.scratch.*;

public class StageTimer {
  public StageTimer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Timer identityChange = new Timer();
    while (true) {
      if (identityChange.every(1)) {
        Color color = myStage.getColor();
        myStage.setColor(color.withHue(color.getHue() + 40));
      }
    }
  }

  public static void main(String[] args) {
    new StageTimer();
  }
}
