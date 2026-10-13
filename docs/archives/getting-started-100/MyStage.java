import org.openpatch.scratch.*;

public class MyStage extends Stage {

  // begin stage designer
  private Bunny bunny;
  // end stage designer

  public MyStage() {
    // begin stage designer
    this.addBackdrop("background");
    bunny = new Bunny();
    this.add(bunny);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(480, 360);
    myWindow.setStage(new MyStage());
  }
}
