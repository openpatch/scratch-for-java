package reference;
import org.openpatch.scratch.*;

public class StageWhenKeyPressed {
  class CustomStage extends Stage {
    public CustomStage() {
      super(600, 240);
    }

    @Override
    public void whenKeyPressed(KeyCode keyCode) {
      this.display("Key Pressed: " + keyCode);
    }
  }

  public StageWhenKeyPressed() {
    new CustomStage();
  }

  public static void main(String[] args) {
    new StageWhenKeyPressed();
  }
}
