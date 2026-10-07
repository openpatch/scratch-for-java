package reference;
import org.openpatch.scratch.*;

public class StageShowVariable {
  public StageShowVariable() {
    class MyStage extends Stage {
      int seconds = 0;

      public MyStage() {
        super(600, 240);
        // A monitor in the top left corner shows the value every frame.
        this.showVariable("seconds", () -> seconds);
      }

      public void run() {
        if (this.getTimer().everyMillis(1000)) {
          seconds++;
        }
      }
    }

    new MyStage();
  }

  public static void main(String[] args) {
    new StageShowVariable();
  }
}
