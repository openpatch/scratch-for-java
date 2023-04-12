import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.*;

public class PenSprite extends Sprite {  
  
  private boolean finished = false;
  private static float color;
  
  public PenSprite() {
    super("pen", "sprites/pen.png");
    this.getPen().down();
    this.getPen().setSize(2);
    color = (float) Math.random() * 255;
    this.getPen().setColor(color);
    this.setOnEdgeBounce(true);
    this.hide();
  }
  
  public static void setColor(float color) {
    PenSprite.color = color;
  }
  
  // when I start as a clone
  public PenSprite(PenSprite pen) {
    super(pen);
    this.setDirection(pen.getDirection() + 90);
    this.getPen().setColor(color);
    if (Math.random() < 0.05) {
      color += Math.random() * 10;
    }
  }
  
  public void setFinished() {
    this.finished = true;
  }

  public void whenKeyPressed(int keyCode) {

    if (keyCode != KeyCode.VK_H) {
      return;
    }
    
    if(this.isVisible()) {
      this.hide();
    } else {
      this.show();
    }
  }
  
  public void run() {
    if (!this.finished) {
      this.move(1);
      if (Math.random() < 0.05) {
        float newRotation = Random.random(4) * 90;
        this.setDirection(newRotation);
      }
    }
  }
}
