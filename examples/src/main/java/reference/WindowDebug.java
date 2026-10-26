package reference;
import org.openpatch.scratch.*;


public class WindowDebug {
  public WindowDebug() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myWindow.setDebug(true);
    // Values written onto the window while it runs. (F12 shows the frame rate.)
    while (true) {
      myWindow.debug("seconds", myStage.getTimer().getSeconds());
      myStage.waitSeconds(0.2);
    }
  }

  public static void main(String[] args) {
    new WindowDebug();
  }
}
