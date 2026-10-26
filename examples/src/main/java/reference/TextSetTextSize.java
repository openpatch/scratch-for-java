package reference;
import org.openpatch.scratch.*;


public class TextSetTextSize {
  public TextSetTextSize() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    // The size is picked from the sizes the font was loaded in, so it steps
    // rather than growing smoothly.
    while (true) {
      myText.setTextSize(16);
      myStage.waitSeconds(1);
      myText.setTextSize(32);
      myStage.waitSeconds(1);
      myText.setTextSize(64);
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new TextSetTextSize();
  }
}
