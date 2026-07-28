package reference;
import org.openpatch.scratch.*;

public class StageChangeTransparency {
  public StageChangeTransparency() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "background");
    // A backdrop starts at 0, fully solid. Stepping up fades it out.
    while (true) {
      for (int i = 0; i < 10; i++) {
        myStage.changeTransparency(10);
        myStage.wait(100);
      }
      myStage.setTransparency(0);
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new StageChangeTransparency();
  }
}
