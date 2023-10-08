package DonutIO;

import org.openpatch.scratch.Sprite;

public class Background extends Sprite {

  public Background() {
    this.addCostume("grid", "DonutIO/assets/grid.png");
  }

  public void run() {
    this.setX(-WorldStage.CAM.getX() % 20);
    this.setY(-WorldStage.CAM.getY() % 20);
  }
}
