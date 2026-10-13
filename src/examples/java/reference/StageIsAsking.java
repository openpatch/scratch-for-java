package reference;
import org.openpatch.scratch.*;


public class StageIsAsking {
  public StageIsAsking() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

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
