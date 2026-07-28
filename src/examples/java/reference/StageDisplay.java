package reference;
import org.openpatch.scratch.*;

public class StageDisplay {
  public StageDisplay() {
    Stage myStage = new Stage(600, 240);
    myStage.display(
        "Welcome! This is a longer text with auto line breaks! So if you write a long sentence it"
            + " should wrap to the next line and the height of the box should increase.",
        2000);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageDisplay();
  }
}
