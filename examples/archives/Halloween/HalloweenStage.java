import org.openpatch.scratch.*;

public class HalloweenStage extends Stage {
  int numberPumpkins = 10;
  HouseSprite house;
  GhostSprite ghost;

  public HalloweenStage() {
    super(400, 400);
    this.addBackdrop("bg", "sprites/background.jpg");
    this.addSound("bg", "sounds/background.wav");

    this.house = new HouseSprite();
    this.add(this.house);

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
