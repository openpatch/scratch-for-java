package DonutIO;

import org.openpatch.scratch.Sprite;

public class Background extends Sprite {

  public Background() {
    this.addCostume("grid", "DonutIO/assets/grid.png");
  }

  public void run() {
    this.getPen().eraseAll();
    this.setX(this.getStage().getCamera().getX() - this.getStage().getCamera().getX() % 40);
    this.setY(this.getStage().getCamera().getY() - this.getStage().getCamera().getY() % 40);
  }
}
