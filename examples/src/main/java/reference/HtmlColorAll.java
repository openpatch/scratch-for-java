package reference;
import org.openpatch.scratch.*;


public class HtmlColorAll {
  public HtmlColorAll() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    // The 140 colours every browser knows, each one a ready-made Color.
    myStage.setColor(HtmlColor.CORNFLOWER_BLUE);
    myStage.waitSeconds(1.5);
    myStage.setColor(HtmlColor.TOMATO);
    myStage.waitSeconds(1.5);
    myStage.setColor(HtmlColor.MEDIUM_SEA_GREEN);
  }

  public static void main(String[] args) {
    new HtmlColorAll();
  }
}
