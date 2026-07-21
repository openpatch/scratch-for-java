import org.openpatch.scratch.*;

public class RaceStage extends Stage {
  public RaceStage() {
    super(600, 340);
    this.addBackdrop("background");

    this.add(new Referee());
    this.add(new Racer("bee", 60, 2.2));
    this.add(new Racer("ladybug", 0, 1.6));
    this.add(new Racer("snail", -60, 1.0));
  }

  public static void main(String[] args) {
    new RaceStage();
  }
}
