package reference;
import org.openpatch.scratch.*;


public class WindowTransitionToStage {
  public WindowTransitionToStage() {
    Window myWindow = new Window(600, 240);
    Stage first = new Stage();
    first.setColor(140);
    Stage second = new Stage();
    second.setColor(20);
    myWindow.setStage(first);
    first.wait(1500);
    // The same as switching, but fading over the given number of milliseconds.
    myWindow.transitionToStage(second, 1000);
  }

  public static void main(String[] args) {
    new WindowTransitionToStage();
  }
}
