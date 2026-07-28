package reference;
import org.openpatch.scratch.*;


public class TextGetStyle {
  public TextGetStyle() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Which way am I drawn?", 0, 0, 400);
    myStage.add(myText);

    // A text starts out plain, and can tell you so.
    System.out.println("It starts as " + myText.getStyle() + ".");
    while (true) {
      for (TextStyle style : TextStyle.values()) {
        myText.setStyle(style);
        myText.showText("This one is " + myText.getStyle());
        myStage.wait(1500);
      }
    }
  }

  public static void main(String[] args) {
    new TextGetStyle();
  }
}
