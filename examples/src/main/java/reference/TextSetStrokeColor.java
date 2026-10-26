package reference;
import org.openpatch.scratch.*;


public class TextSetStrokeColor {
  public TextSetStrokeColor() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setStyle(TextStyle.BOX);
    myText.setTextSize(32);
    // The line around the box.
    myText.setStrokeColor(220, 40, 40);
    myStage.waitSeconds(1.5);
    myText.setStrokeColor(HtmlColor.CORNFLOWER_BLUE);
  }

  public static void main(String[] args) {
    new TextSetStrokeColor();
  }
}
