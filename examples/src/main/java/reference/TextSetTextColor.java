package reference;
import org.openpatch.scratch.*;


public class TextSetTextColor {
  public TextSetTextColor() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setTextSize(32);
    myStage.waitSeconds(1);
    // Red, green and blue between 0 and 255 ...
    myText.setTextColor(220, 40, 40);
    myStage.waitSeconds(1);
    // ... or a colour built from a hue, a saturation and a brightness (0-100) ...
    myText.setTextColor(Color.fromHSB(55, 100, 100));
    myStage.waitSeconds(1);
    // ... or a colour that already exists.
    myText.setTextColor(HtmlColor.CORNFLOWER_BLUE);
  }

  public static void main(String[] args) {
    new TextSetTextColor();
  }
}
