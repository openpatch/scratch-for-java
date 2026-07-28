package reference;
import org.openpatch.scratch.*;


public class StagePreviousBackdrop {
  public StagePreviousBackdrop() {
    Stage myStage = new Stage(600, 240);

    myStage.addBackdrop("first", "background");
    myStage.addBackdrop("second", "UIbg");
    // Backwards through the backdrops, wrapping round to the last one.
    while (true) {
      myStage.previousBackdrop();
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new StagePreviousBackdrop();
  }
}
