package reference;
import org.openpatch.scratch.*;

public class StageTimer {
  public StageTimer() {
    Stage myStage = new Stage(600, 240);
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
