package reference;
import org.openpatch.scratch.*;


public class TextWhenAddedToStage {
  public TextWhenAddedToStage() {
    class MyText extends Text {
      public MyText() {
        super("Hello World", 0, 0, 400);
      }

      // Called once the text is on a stage - the point at which it may look
      // things up about the stage it is on.
      public void whenAddedToStage() {
        this.showText("I am on a stage " + this.getStage().getWidth() + " pixels wide");
      }
    }

    Stage myStage = new Stage(600, 240);
    myStage.add(new MyText());
  }

  public static void main(String[] args) {
    new TextWhenAddedToStage();
  }
}
