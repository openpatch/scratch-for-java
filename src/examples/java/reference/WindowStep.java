package reference;
import org.openpatch.scratch.*;

public class WindowStep {
  public WindowStep() {
    class MyStage extends Stage {
      public MyStage() {
        super(600, 240);
        this.add(new Sprite("slime", "slimeGreen"));
        this.display("P: pause and go on, S: one step while paused");
      }

      public void whenKeyPressed(KeyCode keyCode) {
        Window window = Window.getInstance();
        if (keyCode == KeyCode.P) {
          if (window.isPaused()) {
            window.resume();
          } else {
            window.pause();
          }
        } else if (keyCode == KeyCode.S) {
          // Exactly one frame: every run() once, then paused again.
          window.step();
        }
      }

      public void run() {
        for (Sprite sprite : this.getAll()) {
          sprite.move(5);
          sprite.ifOnEdgeBounce();
        }
      }
    }

    new MyStage();
  }

  public static void main(String[] args) {
    new WindowStep();
  }
}
