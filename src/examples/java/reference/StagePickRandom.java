package reference;
import org.openpatch.scratch.*;

public class StagePickRandom {
  public StagePickRandom() {
    Stage myStage = new Stage(600, 240);
    while (true) {
      int random = myStage.pickRandom(0, 100);
      myStage.display("Random: " + random);
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new StagePickRandom();
  }
}
