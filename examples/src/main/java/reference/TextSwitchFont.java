package reference;
import org.openpatch.scratch.*;


public class TextSwitchFont {
  public TextSwitchFont() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);

    myText.addFont("serif", "serif");
    // Back and forth between the font that was added and the one every text
    // starts with.
    while (true) {
      myText.switchFont("serif");
      myStage.waitSeconds(1.5);
      myText.switchFont(Text.getDefaultFont());
      myStage.waitSeconds(1.5);
    }
  }

  public static void main(String[] args) {
    new TextSwitchFont();
  }
}
