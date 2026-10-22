package reference;
import org.openpatch.scratch.*;


public class TextSetAlign {
  public TextSetAlign() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setStyle(TextStyle.PLAIN);
    myText.setTextSize(32);
    // Where the text sits relative to its position, which stays in the middle
    // of the stage the whole time.
    while (true) {
      myText.setAlign(TextAlign.LEFT);
      myStage.waitSeconds(1.2);
      myText.setAlign(TextAlign.CENTER);
      myStage.waitSeconds(1.2);
      myText.setAlign(TextAlign.RIGHT);
      myStage.waitSeconds(1.2);
    }
  }

  public static void main(String[] args) {
    new TextSetAlign();
  }
}
