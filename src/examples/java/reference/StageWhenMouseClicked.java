package reference;
import org.openpatch.scratch.*;


public class StageWhenMouseClicked {
  public StageWhenMouseClicked() {
    class MyStage extends Stage {
      public MyStage() {
      }

      public void whenMouseClicked(MouseCode mouseCode) {
        this.display("clicked at " + this.getMouseX() + ", " + this.getMouseY());
      }
    }

    Window myWindow = new Window(600, 240);
    myWindow.setStage(new MyStage());
  }

  public static void main(String[] args) {
    new StageWhenMouseClicked();
  }
}
