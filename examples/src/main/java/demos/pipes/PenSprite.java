package demos.pipes;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Random;

public class PenSprite extends Sprite {

  private boolean finished = false;
  private static double color;

  public PenSprite() {
    super("pen", "demos/pipes/sprites/pen.png");
    this.getPen().down();
    this.getPen().setSize(2);
    color = Random.random(100);
    this.getPen().setColor(color);
    this.hide();

    // begin sprite editor
    // end sprite editor
  }

  public static void setColor(double color) {
    PenSprite.color = color;
  }

  @Override
  public void whenStartsAsClone() {
    this.setDirection(this.getDirection() + 90);
    this.getPen().setColor(color);
    if (Math.random() < 0.05) {
      color += Math.random() * 4;
    }
  }

  public void setFinished() {
    this.finished = true;
  }

  public void whenKeyPressed(KeyCode keyCode) {

    if (keyCode != KeyCode.H) {
      return;
    }

    if (this.isVisible()) {
      this.hide();
    } else {
      this.show();
    }
  }

  public void run() {
    if (!this.finished) {
      this.move(1);
      this.ifOnEdgeBounce();
      if (Math.random() < 0.05) {
        int newRotation = Random.randomInt(4) * 90;
        this.setDirection(newRotation);
      }
    }
  }
}
