import org.openpatch.scratch.*;

public class WalkStage extends Stage {
  public WalkStage() {
    super(500, 260);
    this.addBackdrop("background");
    this.add(new Walker());
  }

  public static void main(String[] args) {
    new WalkStage();
  }
}
