package reference;
import org.openpatch.scratch.*;


public class TextRemove {
  public TextRemove() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myStage.wait(2000);
    // Takes the text off the stage again.
    myText.remove();
  }

  public static void main(String[] args) {
    new TextRemove();
  }
}
