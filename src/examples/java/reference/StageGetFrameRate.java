package reference;
import org.openpatch.scratch.*;


public class StageGetFrameRate {
  public StageGetFrameRate() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    while (true) {
      myStage.display("frames per second: " + Operators.round(myStage.getFrameRate()));
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new StageGetFrameRate();
  }
}
