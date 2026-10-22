package reference;
import org.openpatch.scratch.*;


public class WindowGetWidth {
  public WindowGetWidth() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new Stage());
    System.out.println("the window is " + myWindow.getWidth() + " pixels wide");
  }

  public static void main(String[] args) {
    new WindowGetWidth();
  }
}
