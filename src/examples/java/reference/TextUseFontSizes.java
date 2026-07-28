package reference;
import org.openpatch.scratch.*;


public class TextUseFontSizes {
  public TextUseFontSizes() {
    // The sizes the font is loaded in. setTextSize() picks the nearest of them,
    // so a size that is not in the list is rounded to one that is.
    Text.useFontSizes(12, 24, 48);

    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);
    while (true) {
      myText.setTextSize(12);
      myStage.wait(1000);
      myText.setTextSize(24);
      myStage.wait(1000);
      myText.setTextSize(48);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new TextUseFontSizes();
  }
}
