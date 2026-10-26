package reference;
import org.openpatch.scratch.*;


public class WindowTransitionToStage {
  public WindowTransitionToStage() {
    Window myWindow = new Window(600, 240);
    Stage first = new Stage();
    first.setColor(Color.fromHSB(55, 100, 100));
    Stage second = new Stage();
    second.setColor(Color.fromHSB(8, 100, 100));
    myWindow.setStage(first);
    first.waitSeconds(1.5);
    // The same as switching, but fading over the given number of seconds.
    myWindow.transitionToStage(second, 1);
  }

  public static void main(String[] args) {
    new WindowTransitionToStage();
  }
}
