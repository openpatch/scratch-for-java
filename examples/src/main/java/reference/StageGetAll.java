package reference;
import org.openpatch.scratch.*;

public class StageGetAll {
  public StageGetAll() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.add(new Pen());
    myStage.add(new Sprite());
    myStage.add(new Sprite());
    myStage.display("All: " + myStage.getAll().size());
    myStage.waitSeconds(2);
  }

  public static void main(String[] args) {
    new StageGetAll();
  }
}
