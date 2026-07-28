package reference;
import org.openpatch.scratch.*;

public class StageRemove {
  public StageRemove() {
    Stage myStage = new Stage(600, 240);
    Sprite gamma = new Sprite("gamma", "slimePurple");
    myStage.add(gamma);
    myStage.wait(2000);
    myStage.remove(gamma);
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageRemove();
  }
}
