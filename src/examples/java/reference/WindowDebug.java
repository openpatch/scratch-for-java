package reference;
import org.openpatch.scratch.*;


public class WindowDebug {
  public WindowDebug() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myWindow.setDebug(true);
    // Values written onto the window while it runs.
    while (true) {
      myWindow.debug("frames per second", myStage.getFrameRate());
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new WindowDebug();
  }
}
