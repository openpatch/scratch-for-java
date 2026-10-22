package reference;
import org.openpatch.scratch.*;


public class WindowGetHeight {
  public WindowGetHeight() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new Stage());
    System.out.println("the window is " + myWindow.getHeight() + " pixels high");
  }

  public static void main(String[] args) {
    new WindowGetHeight();
  }
}
