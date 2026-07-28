package reference;
import org.openpatch.scratch.*;


public class StageGetAnswer {
  public StageGetAnswer() {
    Stage myStage = new Stage(600, 240);

    myStage.ask("How old are you?");
    while (myStage.isAsking()) {
      myStage.wait(50);
    }
    System.out.println("The answer was " + myStage.getAnswer());
  }

  public static void main(String[] args) {
    new StageGetAnswer();
  }
}
