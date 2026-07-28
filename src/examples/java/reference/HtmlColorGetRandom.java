package reference;
import org.openpatch.scratch.*;


public class HtmlColorGetRandom {
  public HtmlColorGetRandom() {
    Stage myStage = new Stage(600, 240);

    // A different one of the 140 HTML colours every second.
    while (true) {
      myStage.setColor(HtmlColor.getRandom());
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new HtmlColorGetRandom();
  }
}
