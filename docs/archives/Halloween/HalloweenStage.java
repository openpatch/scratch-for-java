import org.openpatch.scratch.*;

public class HalloweenStage extends Stage {

  // begin stage designer
  private HouseSprite house;
  // end stage designer
  private int numberPumpkins = 10;
  private GhostSprite ghost;

  public HalloweenStage() {
    // begin stage designer
    house = new HouseSprite();
    this.add(house);
    // end stage designer

    for (int i = 0; i < this.numberPumpkins; i++) {
      PumpkinSprite p = new PumpkinSprite();
      this.add(p);
    }
    this.ghost = new GhostSprite();
    this.add(this.ghost);
    this.addSound("bg", "sounds/background.wav");
  }

  public void run() {
    this.playSound("bg");
  }

  public static void main(String[] args) {
    Window myWindow = new Window(400, 400);
    myWindow.setStage(new HalloweenStage());
  }
}
