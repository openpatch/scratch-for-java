package reference;
import org.openpatch.scratch.*;

public class StageGetAll {
  public StageGetAll() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new Pen());
    myStage.add(new Sprite());
    myStage.add(new Sprite());
    myStage.display("All: " + myStage.getAll().size());
    myStage.wait(2000);
  }

  public static void main(String[] args) {
    new StageGetAll();
  }
}
