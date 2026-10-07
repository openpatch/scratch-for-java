package reference;
import org.openpatch.scratch.*;

public class WindowPause {
  public WindowPause() {
    class MyStage extends Stage {
      public MyStage() {
        super(600, 240);
        Sprite slime = new Sprite("slime", "slimeGreen");
        this.add(slime);
        this.display("Press P to pause and to go on");
      }

      // Keys still arrive while the game is paused, so P can resume it too.
      public void whenKeyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.P) {
          Window window = Window.getInstance();
          if (window.isPaused()) {
            window.resume();
          } else {
            window.pause();
          }
        }
      }

      public void run() {
        for (Sprite sprite : this.getAll()) {
          sprite.move(3);
          sprite.ifOnEdgeBounce();
        }
      }
    }

    new MyStage();
  }

  public static void main(String[] args) {
    new WindowPause();
  }
}
