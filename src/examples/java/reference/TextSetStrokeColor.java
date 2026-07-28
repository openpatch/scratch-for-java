package reference;
import org.openpatch.scratch.*;


public class TextSetStrokeColor {
  public TextSetStrokeColor() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setStyle(TextStyle.BOX);
    myText.setTextSize(32);
    // The line around the box.
    myText.setStrokeColor(220, 40, 40);
    myStage.wait(1500);
    myText.setStrokeColor(HtmlColor.CORNFLOWER_BLUE);
  }

  public static void main(String[] args) {
    new TextSetStrokeColor();
  }
}
