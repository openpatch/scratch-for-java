package reference;
import org.openpatch.scratch.*;

public class StageRemove {
  public StageRemove() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
