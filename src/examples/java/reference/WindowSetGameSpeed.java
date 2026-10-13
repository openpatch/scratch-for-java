package reference;
import org.openpatch.scratch.*;

public class WindowSetGameSpeed {
  public WindowSetGameSpeed() {
    class MyStage extends Stage {
      public MyStage() {
        this.add(new Sprite("slime", "slimeGreen"));
        this.display("1: normal speed, 2: half speed, 3: slow motion");
      }

      public void whenKeyPressed(KeyCode keyCode) {
        Window window = Window.getInstance();
        if (keyCode == KeyCode.DIGIT_1) {
          window.setGameSpeed(1);
        } else if (keyCode == KeyCode.DIGIT_2) {
          window.setGameSpeed(0.5);
        } else if (keyCode == KeyCode.DIGIT_3) {
          window.setGameSpeed(0.1);
        }
      }

      public void run() {
        for (Sprite sprite : this.getAll()) {
          sprite.move(5);
          sprite.ifOnEdgeBounce();
        }
      }
    }

    Window myWindow = new Window(600, 240);
    myWindow.setStage(new MyStage());
  }

  public static void main(String[] args) {
    new WindowSetGameSpeed();
  }
}
