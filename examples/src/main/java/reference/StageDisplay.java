package reference;
import org.openpatch.scratch.*;

public class StageDisplay {
  public StageDisplay() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.display("Welcome! This is a longer text with auto line breaks! So if you write a long sentence it"
            + " should wrap to the next line and the height of the box should increase.", 2);
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new StageDisplay();
  }
}
