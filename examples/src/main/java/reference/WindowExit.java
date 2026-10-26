package reference;
import org.openpatch.scratch.*;

public class WindowExit {
  public WindowExit() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.waitSeconds(3);
    // Closes the window and ends the program.
    myWindow.exit();
  }

  public static void main(String[] args) {
    new WindowExit();
  }
}
