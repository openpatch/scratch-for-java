package reference;
import org.openpatch.scratch.*;


public class TextSetTextSize {
  public TextSetTextSize() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    // The size is picked from the sizes the font was loaded in, so it steps
    // rather than growing smoothly.
    while (true) {
      myText.setTextSize(16);
      myStage.wait(1000);
      myText.setTextSize(32);
      myStage.wait(1000);
      myText.setTextSize(64);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new TextSetTextSize();
  }
}
