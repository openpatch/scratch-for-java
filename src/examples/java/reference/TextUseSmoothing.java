package reference;
import org.openpatch.scratch.*;


public class TextUseSmoothing {
  public TextUseSmoothing() {
    // Smoothing off keeps the letters crisp, which suits a pixel-art font. It
    // has to be chosen before the first stage is built.
    Text.useSmoothing(false);

    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(48);
    myStage.add(myText);
  }

  public static void main(String[] args) {
    new TextUseSmoothing();
  }
}
