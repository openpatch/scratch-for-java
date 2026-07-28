package reference;
import org.openpatch.scratch.*;


public class TextSetWidth {
  public TextSetWidth() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setStyle(TextStyle.BOX);
    myText.showText("A line long enough to have to wrap somewhere");
    // How wide the text may grow before it wraps onto the next line.
    while (true) {
      myText.setWidth(400);
      myStage.wait(1500);
      myText.setWidth(150);
      myStage.wait(1500);
    }
  }

  public static void main(String[] args) {
    new TextSetWidth();
  }
}
