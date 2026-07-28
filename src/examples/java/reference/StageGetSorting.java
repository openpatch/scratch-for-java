package reference;
import org.openpatch.scratch.*;


public class StageGetSorting {
  public StageGetSorting() {
    Stage myStage = new Stage(600, 240);

    // Sorting decides the order the sprites are drawn in. Only the desktop
    // version can do it - in the browser this prints a notice.
    myStage.getSorting().byY();
  }

  public static void main(String[] args) {
    new StageGetSorting();
  }
}
