package reference;
import org.openpatch.scratch.*;

public class StageGetDeltaTime {
  public StageGetDeltaTime() {
    Stage myStage = new Stage(600, 240);
    while (true) {
      var dt = myStage.getDeltaTime();
      myStage.display("Delta Time: " + dt);
    }
  }

  public static void main(String[] args) {
    new StageGetDeltaTime();
  }
}
