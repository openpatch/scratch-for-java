import org.openpatch.scratch.*;

public class RaceStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private Referee referee;
  // scratch4j:end fields

  public RaceStage() {
    super(600, 340);

    // scratch4j:begin setup (managed by the stage designer)
    this.addBackdrop("background");
    referee = new Referee();
    this.add(referee);
    // scratch4j:end setup
    this.add(new Racer("bee", 60, 2.2));
    this.add(new Racer("ladybug", 0, 1.6));
    this.add(new Racer("snail", -60, 1.0));
  }

  public static void main(String[] args) {
    new RaceStage();
  }
}
