package reference;
import org.openpatch.scratch.*;


public class TextSetBackgroundColor {
  public TextSetBackgroundColor() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    // The background is only drawn by the styles that have one.
    myText.setStyle(TextStyle.BOX);
    myText.setTextSize(32);
    myStage.wait(1000);
    myText.setBackgroundColor(40, 40, 60);
    myText.setTextColor(255, 255, 255);
    myStage.wait(1000);
    myText.setBackgroundColor(HtmlColor.GOLD);
    myText.setTextColor(60, 40, 0);
  }

  public static void main(String[] args) {
    new TextSetBackgroundColor();
  }
}
