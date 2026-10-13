package reference;
import org.openpatch.scratch.*;

public class StageGetDeltaTime {
  public StageGetDeltaTime() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    while (true) {
      var dt = myStage.getDeltaTime();
      myStage.display("Delta Time: " + dt);
    }
  }

  public static void main(String[] args) {
    new StageGetDeltaTime();
  }
}
