package reference;
import org.openpatch.scratch.*;


public class StageAsk {
  public StageAsk() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

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
