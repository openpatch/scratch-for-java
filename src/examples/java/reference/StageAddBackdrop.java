package reference;
import org.openpatch.scratch.Stage;

public class StageAddBackdrop {
  public StageAddBackdrop() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "background", true);
    myStage.wait(1000);
  }

  public static void main(String[] args) {
    new StageAddBackdrop();
  }
}
