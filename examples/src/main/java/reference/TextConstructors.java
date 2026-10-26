package reference;
import org.openpatch.scratch.*;


public class TextConstructors {
  public TextConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    // A text needs the words, where it sits and how wide it may grow before it
    // wraps onto the next line.
    Text myText = new Text("Hello World", 0, 60, 400);
    myStage.add(myText);

    // A text built without any of that starts out empty, in the middle.
    Text emptyText = new Text();
    emptyText.setY(-60);
    emptyText.showText("... and hello again");
    myStage.add(emptyText);
  }

  public static void main(String[] args) {
    new TextConstructors();
  }
}
