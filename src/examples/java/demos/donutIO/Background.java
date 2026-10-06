package demos.donutIO;

import org.openpatch.scratch.Sprite;

public class Background extends Sprite {

  public Background() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("grid", "demos/donutIO/assets/grid.png");
    // scratch4j:end setup
  }

  public void run() {
    this.getPen().eraseAll();
    this.setX(this.getStage().getCamera().getX() - this.getStage().getCamera().getX() % 40);
    this.setY(this.getStage().getCamera().getY() - this.getStage().getCamera().getY() % 40);
  }
}
