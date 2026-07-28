package reference;
import org.openpatch.scratch.*;


public class TextSetAlign {
  public TextSetAlign() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setStyle(TextStyle.PLAIN);
    myText.setTextSize(32);
    // Where the text sits relative to its position, which stays in the middle
    // of the stage the whole time.
    while (true) {
      myText.setAlign(TextAlign.LEFT);
      myStage.wait(1200);
      myText.setAlign(TextAlign.CENTER);
      myStage.wait(1200);
      myText.setAlign(TextAlign.RIGHT);
      myStage.wait(1200);
    }
  }

  public static void main(String[] args) {
    new TextSetAlign();
  }
}
