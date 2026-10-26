package reference;
import org.openpatch.scratch.*;


public class WindowGetLibraryTitle {
  public WindowGetLibraryTitle() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new Stage());
    System.out.println("This is " + myWindow.getLibraryTitle() + ".");
  }

  public static void main(String[] args) {
    new WindowGetLibraryTitle();
  }
}
