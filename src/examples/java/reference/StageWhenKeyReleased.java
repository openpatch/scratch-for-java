package reference;
import org.openpatch.scratch.*;


public class StageWhenKeyReleased {
  public StageWhenKeyReleased() {
    class MyStage extends Stage {
      public MyStage() {
        super(600, 240);
      }

      // Click into the stage first, so that it takes the keyboard.
      public void whenKeyReleased(KeyCode keyCode) {
        if (keyCode == KeyCode.SPACE) {
          this.display("let go of space");
        }
      }
    }

    new MyStage();
  }

  public static void main(String[] args) {
    new StageWhenKeyReleased();
  }
}
