import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteWhenBackdropSwitches {

  public SpriteWhenBackdropSwitches() {
    Stage myStage = new Stage(254, 100);
    myStage.addBackdrop("forest", "assets/background_forest.png");
    myStage.addBackdrop("sea", "assets/background_sea.png");
    myStage.addTimer("backdrop");
    myStage.add(new CustomSprite());

    GifRecorder recorder = new GifRecorder(
        "" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {
      if (myStage.getTimer("backdrop").intervalMillis(1000)) {
        myStage.switchBackdrop("sea");
      } else {
        myStage.switchBackdrop("forest");
      }
    }
    recorder.stop();
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpriteWhenBackdropSwitches();
  }
}

class CustomSprite extends Sprite {

  public CustomSprite() {
    this.addCostume("zeta", "assets/zeta_green_badge.png");
    this.addCostume("gamma", "assets/gamma_purple_badge.png");
  }

  @Override
  public void whenBackdropSwitches(String name) {
    if (name.equals("forest")) {
      this.switchCostume("zeta");
    } else if (name.equals("sea")) {
      this.switchCostume("gamma");
    }
  }
}
