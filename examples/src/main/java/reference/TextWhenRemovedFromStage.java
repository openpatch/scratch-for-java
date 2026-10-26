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

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    MyText myText = new MyText();
    myStage.add(myText);
    myStage.waitSeconds(2);
    myText.remove();
  }

  public static void main(String[] args) {
    new TextWhenRemovedFromStage();
  }
}
