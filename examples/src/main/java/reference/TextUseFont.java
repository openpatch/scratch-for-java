package reference;
import org.openpatch.scratch.*;


public class TextUseFont {
  public TextUseFont() {
    // One font for every text in the program, chosen before the first stage
    // is built - the fonts are loaded as the window opens.
    Text.useFont("serif");

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);
  }

  public static void main(String[] args) {
    new TextUseFont();
  }
}
