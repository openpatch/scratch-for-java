package reference;
import org.openpatch.scratch.*;


public class StageIsDebug {
  public StageIsDebug() {
    Stage myStage = new Stage(600, 240);

    myStage.setDebug(true);
    System.out.println("Debug mode is on: " + myStage.isDebug());
  }

  public static void main(String[] args) {
    new StageIsDebug();
  }
}
