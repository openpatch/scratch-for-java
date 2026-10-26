import org.openpatch.scratch.*;

public class MyStage extends Stage {

  // begin stage designer
  // end stage designer
  public MyStage() {
    this.add(new Cat());

    // begin stage designer
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(480, 360);
    myWindow.setStage(new MyStage());
  }
}
