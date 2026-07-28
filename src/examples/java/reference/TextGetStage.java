package reference;
import org.openpatch.scratch.*;


public class TextGetStage {
  public TextGetStage() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    // A text knows the stage it was added to.
    System.out.println("The stage is " + myText.getStage().getWidth() + " pixels wide.");
  }

  public static void main(String[] args) {
    new TextGetStage();
  }
}
