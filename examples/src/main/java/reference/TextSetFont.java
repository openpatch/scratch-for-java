package reference;
import org.openpatch.scratch.*;


public class TextSetFont {
  public TextSetFont() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);

    myText.addFont("serif", "serif");
    // setFont() is switchFont() under another name.
    myText.setFont("serif");
    System.out.println("The text is written in " + myText.getFont() + ".");
  }

  public static void main(String[] args) {
    new TextSetFont();
  }
}
