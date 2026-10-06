import org.openpatch.scratch.*;

public class HalloweenStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private HouseSprite house;
  // scratch4j:end fields
  private int numberPumpkins = 10;
  private GhostSprite ghost;

  public HalloweenStage() {
    super(400, 400);
    // scratch4j:begin setup (managed by the stage designer)
    house = new HouseSprite();
    this.add(house);
    // scratch4j:end setup

    for (int i = 0; i < this.numberPumpkins; i++) {
      PumpkinSprite p = new PumpkinSprite();
      this.add(p);
    }
    this.ghost = new GhostSprite();
    this.add(this.ghost);
  }

  public void run() {
    this.playSound("bg");
  }

  public static void main(String[] args) {
    new HalloweenStage();
  }
}
