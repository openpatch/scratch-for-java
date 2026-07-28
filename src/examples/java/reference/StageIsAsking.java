package reference;
import org.openpatch.scratch.*;


public class StageIsAsking {
  public StageIsAsking() {
    Stage myStage = new Stage(600, 240);

    myStage.ask("Are you still there?");
    // True for as long as the box is on the stage waiting to be filled in.
    while (myStage.isAsking()) {
      myStage.wait(50);
    }
    myStage.display("Thank you");
  }

  public static void main(String[] args) {
    new StageIsAsking();
  }
}
