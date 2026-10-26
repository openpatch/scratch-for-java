package reference;
import org.openpatch.scratch.*;

public class StageWhenKeyPressed {
  class CustomStage extends Stage {
    public CustomStage() {
    }

    @Override
    public void whenKeyPressed(KeyCode keyCode) {
      this.display("Key Pressed: " + keyCode);
    }
  }

  public StageWhenKeyPressed() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new CustomStage());
  }

  public static void main(String[] args) {
    new StageWhenKeyPressed();
  }
}
