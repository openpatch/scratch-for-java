package reference;
import org.openpatch.scratch.*;


public class TextNextFont {
  public TextNextFont() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);

    myText.addFont("serif", "serif");
    // Round the list of fonts one at a time, back to the first one at the end.
    while (true) {
      myText.nextFont();
      myText.showText(myText.getCurrentFontName());
      myStage.wait(1500);
    }
  }

  public static void main(String[] args) {
    new TextNextFont();
  }
}
