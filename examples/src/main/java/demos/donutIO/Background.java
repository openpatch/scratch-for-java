package demos.donutIO;

import org.openpatch.scratch.Sprite;

public class Background extends Sprite {

  public Background() {
    // begin sprite editor
    this.addCostume("grid", "demos/donutIO/assets/grid.png");
    // end sprite editor
    // scenery: the grid covers the stage, so it must not count as touching anything
    this.disableHitbox();
  }

  public void run() {
    this.setX(this.getStage().getCamera().getX() - this.getStage().getCamera().getX() % 40);
    this.setY(this.getStage().getCamera().getY() - this.getStage().getCamera().getY() % 40);
  }
}
