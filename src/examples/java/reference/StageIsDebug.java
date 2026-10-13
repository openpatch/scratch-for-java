package reference;
import org.openpatch.scratch.*;


public class StageIsDebug {
  public StageIsDebug() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.setDebug(true);
    System.out.println("Debug mode is on: " + myStage.isDebug());
  }

  public static void main(String[] args) {
    new StageIsDebug();
  }
}
