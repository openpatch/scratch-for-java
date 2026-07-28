package reference;
import org.openpatch.scratch.*;


public class HtmlColorAll {
  public HtmlColorAll() {
    Stage myStage = new Stage(600, 240);

    // The 140 colours every browser knows, each one a ready-made Color.
    myStage.setColor(HtmlColor.CORNFLOWER_BLUE);
    myStage.wait(1500);
    myStage.setColor(HtmlColor.TOMATO);
    myStage.wait(1500);
    myStage.setColor(HtmlColor.MEDIUM_SEA_GREEN);
  }

  public static void main(String[] args) {
    new HtmlColorAll();
  }
}
