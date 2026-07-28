package reference;
import org.openpatch.scratch.*;


public class StageRandomBackdrop {
  public StageRandomBackdrop() {
    Stage myStage = new Stage(600, 240);

    myStage.addBackdrop("first", "background");
    myStage.addBackdrop("second", "UIbg");
    while (true) {
      myStage.randomBackdrop();
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new StageRandomBackdrop();
  }
}
