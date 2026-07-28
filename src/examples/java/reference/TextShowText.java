package reference;
import org.openpatch.scratch.*;


public class TextShowText {
  public TextShowText() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myStage.wait(1500);
    myText.showText("Something else");
    myStage.wait(1500);
    // With a time limit the text takes itself away again.
    myText.showText("Only for a moment", 1500);
  }

  public static void main(String[] args) {
    new TextShowText();
  }
}
