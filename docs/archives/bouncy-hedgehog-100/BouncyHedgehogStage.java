import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class BouncyHedgehogStage extends Stage {

  // begin stage designer
  private HedgehogSprite hedgehogSprite;
  private TrampolineSprite trampolineSprite;
  // end stage designer

  public BouncyHedgehogStage() {
    // begin stage designer
    this.addBackdrop("playground", "playground.jpg");
    hedgehogSprite = new HedgehogSprite();
    this.add(hedgehogSprite);
    trampolineSprite = new TrampolineSprite();
    this.add(trampolineSprite);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(480, 360);
    myWindow.setStage(new BouncyHedgehogStage());
  }
}
