package reference;
import org.openpatch.scratch.*;

public class StageHideVariable {
  public StageHideVariable() {
    class MyStage extends Stage {
      int clicks = 0;

      public MyStage() {
        this.showVariable("clicks", () -> clicks);
      }

      public void whenMouseClicked(MouseCode mouseCode) {
        clicks++;
        // The monitor goes away after five clicks.
        if (clicks == 5) {
          this.hideVariable("clicks");
        }
      }
    }

    Window myWindow = new Window(600, 240);
    myWindow.setStage(new MyStage());
  }

  public static void main(String[] args) {
    new StageHideVariable();
  }
}
