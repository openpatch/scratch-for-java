package reference;
import org.openpatch.scratch.*;


public class StageGetFrameRate {
  public StageGetFrameRate() {
    Stage myStage = new Stage(600, 240);

    while (true) {
      myStage.display("frames per second: " + Operators.round(myStage.getFrameRate()));
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new StageGetFrameRate();
  }
}
