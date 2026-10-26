package reference;
import org.openpatch.scratch.*;


public class HtmlColorGetRandom {
  public HtmlColorGetRandom() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    // A different one of the 140 HTML colours every second.
    while (true) {
      myStage.setColor(HtmlColor.getRandom());
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new HtmlColorGetRandom();
  }
}
