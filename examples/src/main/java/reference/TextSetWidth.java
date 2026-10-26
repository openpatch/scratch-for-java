package reference;
import org.openpatch.scratch.*;


public class TextSetWidth {
  public TextSetWidth() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setStyle(TextStyle.BOX);
    myText.showText("A line long enough to have to wrap somewhere");
    // How wide the text may grow before it wraps onto the next line.
    while (true) {
      myText.setWidth(400);
      myStage.waitSeconds(1.5);
      myText.setWidth(150);
      myStage.waitSeconds(1.5);
    }
  }

  public static void main(String[] args) {
    new TextSetWidth();
  }
}
