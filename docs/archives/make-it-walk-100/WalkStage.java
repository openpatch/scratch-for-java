import org.openpatch.scratch.*;

public class WalkStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private Walker walker;
  // scratch4j:end fields

  public WalkStage() {
    super(500, 260);
    // scratch4j:begin setup (managed by the stage designer)
    this.addBackdrop("background");
    walker = new Walker();
    this.add(walker);
    // scratch4j:end setup
  }

  public static void main(String[] args) {
    new WalkStage();
  }
}
