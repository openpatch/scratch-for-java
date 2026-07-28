package reference;
import org.openpatch.scratch.*;


public class StageAsk {
  public StageAsk() {
    Stage myStage = new Stage(600, 240);

    myStage.ask("What is your name?");
    while (myStage.isAsking()) {
      myStage.wait(50);
    }
    myStage.display("Hello " + myStage.getAnswer());
  }

  public static void main(String[] args) {
    new StageAsk();
  }
}
