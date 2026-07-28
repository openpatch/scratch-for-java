package reference;
import org.openpatch.scratch.*;


public class TextSetTextColor {
  public TextSetTextColor() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setTextSize(32);
    myStage.wait(1000);
    // Red, green and blue between 0 and 255 ...
    myText.setTextColor(220, 40, 40);
    myStage.wait(1000);
    // ... or a hue between 0 and 255 ...
    myText.setTextColor(140);
    myStage.wait(1000);
    // ... or a colour that already exists.
    myText.setTextColor(HtmlColor.CORNFLOWER_BLUE);
  }

  public static void main(String[] args) {
    new TextSetTextColor();
  }
}
