package reference;
import org.openpatch.scratch.*;


public class TextGetCurrentFontName {
  public TextGetCurrentFontName() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myText.setTextSize(32);
    myStage.add(myText);

    myText.addFont("serif", "serif");
    myText.switchFont("serif");
    System.out.println("The text is written in " + myText.getCurrentFontName() + ".");
  }

  public static void main(String[] args) {
    new TextGetCurrentFontName();
  }
}
