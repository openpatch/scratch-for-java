package reference;
import org.openpatch.scratch.*;


public class StageGetAnswer {
  public StageGetAnswer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.ask("How old are you?");
    while (myStage.isAsking()) {
      myStage.waitSeconds(0.05);
    }
    System.out.println("The answer was " + myStage.getAnswer());
  }

  public static void main(String[] args) {
    new StageGetAnswer();
  }
}
