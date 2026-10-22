package reference;
import org.openpatch.scratch.*;


public class WindowGetLibraryVersion {
  public WindowGetLibraryVersion() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new Stage());
    System.out.println(myWindow.getLibraryTitle() + " " + myWindow.getLibraryVersion());
  }

  public static void main(String[] args) {
    new WindowGetLibraryVersion();
  }
}
