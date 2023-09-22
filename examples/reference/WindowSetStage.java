import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.timer.Timer;

public class WindowSetStage {
  public WindowSetStage() {
    Window myWindow = new Window();

    Stage firstLevel = new Stage();
    Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
    firstLevel.add(gamma);

    Stage secondLevel = new Stage();
    Sprite zeta = new Sprite("zeta", "assets/zeta_green_badge.png");
    secondLevel.add(zeta);

    Timer switchTimer = new Timer();

    while (switchTimer.forMillis(2000)) {
      myWindow.setStage(secondLevel);
    }

    switchTimer.reset();

    while (switchTimer.forMillis(2000)) {
      myWindow.setStage(firstLevel);
    }

    myWindow.exit();
  }

  public static void main(String[] args) {
    new WindowSetStage();
  }
}
