package reference;
import org.openpatch.scratch.*;


public class TextShowText {
  public TextShowText() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myStage.waitSeconds(1.5);
    myText.showText("Something else");
    myStage.waitSeconds(1.5);
    // With a time limit the text takes itself away again.
    myText.showText("Only for a moment", 1.5);
  }

  public static void main(String[] args) {
    new TextShowText();
  }
}
