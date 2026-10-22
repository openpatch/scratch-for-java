package reference;
import org.openpatch.scratch.*;


public class TextGetFont {
  public TextGetFont() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);

    System.out.println("The text is written in " + myText.getFont() + ".");
  }

  public static void main(String[] args) {
    new TextGetFont();
  }
}
