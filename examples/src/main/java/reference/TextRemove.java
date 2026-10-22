package reference;
import org.openpatch.scratch.*;


public class TextRemove {
  public TextRemove() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myStage.waitSeconds(2);
    // Takes the text off the stage again.
    myText.remove();
  }

  public static void main(String[] args) {
    new TextRemove();
  }
}
