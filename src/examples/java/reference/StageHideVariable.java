package reference;
import org.openpatch.scratch.*;

public class StageHideVariable {
  public StageHideVariable() {
    class MyStage extends Stage {
      int clicks = 0;

      public MyStage() {
        super(600, 240);
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

    new MyStage();
  }

  public static void main(String[] args) {
    new StageHideVariable();
  }
}
