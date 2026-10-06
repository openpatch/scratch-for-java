import org.openpatch.scratch.Stage;

public class BouncyHedgehogStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private HedgehogSprite hedgehogSprite;
  private TrampolineSprite trampolineSprite;
  // scratch4j:end fields

  public BouncyHedgehogStage() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addBackdrop("playground", "playground.jpg");
    hedgehogSprite = new HedgehogSprite();
    this.add(hedgehogSprite);
    trampolineSprite = new TrampolineSprite();
    this.add(trampolineSprite);
    // scratch4j:end setup
  }

  public static void main(String[] args) {
    new BouncyHedgehogStage();
  }
}
