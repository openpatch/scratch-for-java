package reference;
import org.openpatch.scratch.*;


public class TextSwitchFont {
  public TextSwitchFont() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);

    myText.addFont("serif", "serif");
    // Back and forth between the font that was added and the one every text
    // starts with.
    while (true) {
      myText.switchFont("serif");
      myStage.wait(1500);
      myText.switchFont(Text.getDefaultFont());
      myStage.wait(1500);
    }
  }

  public static void main(String[] args) {
    new TextSwitchFont();
  }
}
