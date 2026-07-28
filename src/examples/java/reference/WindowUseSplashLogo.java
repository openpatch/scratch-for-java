package reference;
import org.openpatch.scratch.*;


public class WindowUseSplashLogo {
  public WindowUseSplashLogo() {
    // The picture shown while the window is starting up - a file next to the
    // program, or a built-in sprite. It has to be said before the window is
    // built.
    Window.useSplashLogo("slimeGreen");
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new Stage());
  }

  public static void main(String[] args) {
    new WindowUseSplashLogo();
  }
}
