import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.timer.Timer;

public class WindowSwitchStage {
  public WindowSwitchStage() {
    Window myWindow = new Window("assets");

    Stage firstLevel = new Stage();
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    firstLevel.add(gamma);
    myWindow.addStage("first", firstLevel);

    Stage secondLevel = new Stage();
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    secondLevel.add(zeta);
    myWindow.addStage("second", secondLevel);

    Timer switchTimer = new Timer();

    while (switchTimer.forMillis(2000)) {
      myWindow.switchStage("first");
    }

    switchTimer.reset();

    while (switchTimer.forMillis(2000)) {
      myWindow.switchStage("second");
    }
  }

  public static void main(String[] args) {
    new WindowSwitchStage();
  }
}
