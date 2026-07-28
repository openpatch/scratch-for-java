package reference;
import org.openpatch.scratch.*;

public class StageWhenBackdropSwitches {
  public StageWhenBackdropSwitches() {
    Stage myStage = new CustomStage();
    myStage.wait(1000);
    myStage.nextBackdrop();
    myStage.wait(1000);
    myStage.nextBackdrop();
    myStage.wait(1000);
  }

  class CustomStage extends Stage {
    public CustomStage() {
      super(600, 240);
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
