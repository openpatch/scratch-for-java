package reference;
import org.openpatch.scratch.*;

public class WindowSetStage {
  public WindowSetStage() {
    Window myWindow = new Window(500, 200);

    Stage firstLevel = new Stage();
    Sprite gamma = new Sprite("gamma", "slimePurple");
    firstLevel.add(gamma);

    Stage secondLevel = new Stage();
    Sprite zeta = new Sprite("zeta", "slimeGreen");
    secondLevel.add(zeta);

    // The window shows one stage at a time. Setting another puts it on screen
    // in place of the one before it.
    while (true) {
      myWindow.setStage(secondLevel);
      secondLevel.wait(2000);
      myWindow.setStage(firstLevel);
      firstLevel.wait(2000);
    }
  }

  public static void main(String[] args) {
    new WindowSetStage();
  }
}
