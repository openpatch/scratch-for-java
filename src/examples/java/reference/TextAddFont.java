package reference;
import org.openpatch.scratch.*;


public class TextAddFont {
  public TextAddFont() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);

    // A font under a name of its own. It can be a font file next to the
    // program, or the name of a family the system already has.
    myText.addFont("serif", "serif");
    myText.switchFont("serif");
  }

  public static void main(String[] args) {
    new TextAddFont();
  }
}
