import org.openpatch.scratch.Window;

public class MyWindow extends Window {
  public MyWindow() {
    super(800, 600, "assets");

    this.setStage(new MyStage());
  }

  public static void main(String[] args) {
    new MyWindow();
  }
}
