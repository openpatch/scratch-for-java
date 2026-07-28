package reference;
import org.openpatch.scratch.*;


public class TextGetCurrentFontIndex {
  public TextGetCurrentFontIndex() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);

    myText.addFont("serif", "serif");
    myText.switchFont("serif");
    System.out.println("That is font number " + myText.getCurrentFontIndex() + ".");
  }

  public static void main(String[] args) {
    new TextGetCurrentFontIndex();
  }
}
