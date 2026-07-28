package reference;
import org.openpatch.scratch.*;


public class TextGetAlign {
  public TextGetAlign() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setAlign(TextAlign.LEFT);
    System.out.println("The text is aligned " + myText.getAlign() + ".");
  }

  public static void main(String[] args) {
    new TextGetAlign();
  }
}
