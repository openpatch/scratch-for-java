package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteWhenBackdropSwitches {
  public SpriteWhenBackdropSwitches() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "background");
    myStage.addBackdrop("sea", "UIbg");
    myStage.add(new CustomSprite());
    while (true) {
      if (myStage.getTimer("backdrop").intervalMillis(1000)) {
        myStage.switchBackdrop("sea");
      } else {
        myStage.switchBackdrop("forest");
      }
    }
  }

  public static void main(String[] args) {
    new SpriteWhenBackdropSwitches();
  }
}

class CustomSprite extends Sprite {
  public CustomSprite() {
    this.addCostume("zeta", "slimeGreen");
    this.addCostume("gamma", "slimePurple");
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
