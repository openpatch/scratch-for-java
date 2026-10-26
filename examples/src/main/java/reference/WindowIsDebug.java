package reference;
import org.openpatch.scratch.*;


public class WindowIsDebug {
  public WindowIsDebug() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new Stage());
    myWindow.setDebug(true);
    System.out.println("debug mode is on: " + myWindow.isDebug());
  }

  public static void main(String[] args) {
    new WindowIsDebug();
  }
}
