package reference;
import org.openpatch.scratch.*;

public class WindowWhenExits {
  public WindowWhenExits() {
    class MyWindow extends Window {
      public MyWindow() {
        super(600, 240);
        this.setStage(new Stage());
      }

      // The last thing that happens before the program ends - the place to save
      // a high score.
      public void whenExits() {
        System.out.println("Goodbye");
      }
    }

    MyWindow myWindow = new MyWindow();
    myWindow.getStage().wait(3000);
    myWindow.exit();
  }

  public static void main(String[] args) {
    new WindowWhenExits();
  }
}
