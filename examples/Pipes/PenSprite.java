import eu.barkmin.processing.scratch.*;

public class PenSprite extends ScratchSprite {  
  
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
    this.setRotation(pen.getRotation() + 90);
    this.getPen().setColor(color);
    if (Math.random() < 0.05) {
      color += Math.random() * 10;
    }
  }
  
  public void setFinished() {
    this.finished = true;
  }
  
  // see https://docs.oracle.com/javase/6/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_ACCEPT for keyCodes
  public void keyPressed(int keyCode) {
    // if keyCode is not 72 (h) do nothing
    if (keyCode != 72) {
      return;
    }
    
    if(this.isVisible()) {
      this.hide();
    } else {
      this.show();
    }
  }
  
  public void draw() {
    if (!this.finished) {
      super.draw();
      this.move(1);
      if (Math.random() < 0.05) {
        float newRotation = Math.round(Math.random() * 4) * 90;
        this.setRotation(newRotation);
      }
    }
  }
}
