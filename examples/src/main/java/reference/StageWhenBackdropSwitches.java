package reference;
import org.openpatch.scratch.*;

public class StageWhenBackdropSwitches {
  public StageWhenBackdropSwitches() {
    Stage myStage = new CustomStage();
    myStage.waitSeconds(1);
    myStage.nextBackdrop();
    myStage.waitSeconds(1);
    myStage.nextBackdrop();
    myStage.waitSeconds(1);
  }

  class CustomStage extends Stage {
    public CustomStage() {
      this.addBackdrop("forest", "background");
      this.addBackdrop("sea", "UIbg");
    }

    public void whenBackdropSwitches(String name) {
      if (name.equals("sea")) {
        this.display("Sea");
      } else if (name.equals("forest")) {
        this.display("Team Trees!");
      }
    }
  }

  public static void main(String[] args) {
    new StageWhenBackdropSwitches();
  }
}
