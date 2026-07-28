package reference;
import org.openpatch.scratch.*;


public class WindowUseFullScreen {
  public WindowUseFullScreen() {
    // Has to be said before the window is built, because it decides how the
    // window is made. Only the desktop version can go fullscreen.
    Window.useFullScreen();
    Window myWindow = new Window();
    myWindow.setStage(new Stage());
  }

  public static void main(String[] args) {
    new WindowUseFullScreen();
  }
}
