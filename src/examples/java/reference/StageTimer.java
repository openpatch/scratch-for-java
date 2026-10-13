package reference;
import org.openpatch.scratch.*;

public class StageTimer {
  public StageTimer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    while (true) {
      if (myStage.getTimer("identityChange").everyMillis(1000)) {
        myStage.changeColor(40);
      }
    }
  }

  public static void main(String[] args) {
    new StageTimer();
  }
}
