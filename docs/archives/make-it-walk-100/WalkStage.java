import org.openpatch.scratch.*;

public class WalkStage extends Stage {

  // begin stage designer
  private Walker walker;
  // end stage designer

  public WalkStage() {
    // begin stage designer
    this.addBackdrop("background");
    walker = new Walker();
    this.add(walker);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(500, 260);
    myWindow.setStage(new WalkStage());
  }
}
