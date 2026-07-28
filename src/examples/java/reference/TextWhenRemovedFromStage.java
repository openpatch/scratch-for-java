package reference;
import org.openpatch.scratch.*;


public class TextWhenRemovedFromStage {
  public TextWhenRemovedFromStage() {
    class MyText extends Text {
      public MyText() {
        super("Hello World", 0, 0, 400);
      }

      // Called as the text is taken off the stage again.
      public void whenRemovedFromStage() {
        System.out.println("Goodbye");
      }
    }

    Stage myStage = new Stage(600, 240);
    MyText myText = new MyText();
    myStage.add(myText);
    myStage.wait(2000);
    myText.remove();
  }

  public static void main(String[] args) {
    new TextWhenRemovedFromStage();
  }
}
