package reference;
import org.openpatch.scratch.*;


public class WindowGetInstance {
  public WindowGetInstance() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new Stage());
    // There is only ever one window, and this is how anything reaches it.
    System.out.println("the window is " + Window.getInstance().getWidth() + " pixels wide");
  }

  public static void main(String[] args) {
    new WindowGetInstance();
  }
}
