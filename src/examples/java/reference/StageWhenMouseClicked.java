package reference;
import org.openpatch.scratch.*;


public class StageWhenMouseClicked {
  public StageWhenMouseClicked() {
    class MyStage extends Stage {
      public MyStage() {
        super(600, 240);
      }

      public void whenMouseClicked(MouseCode mouseCode) {
        this.display("clicked at " + this.getMouseX() + ", " + this.getMouseY());
      }
    }

    new MyStage();
  }

  public static void main(String[] args) {
    new StageWhenMouseClicked();
  }
}
