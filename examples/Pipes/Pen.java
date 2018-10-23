import eu.barkmin.processing.scratch.*;

public class Pen extends ScratchSprite {  
  
  private boolean finished = false;
  
  public Pen() {
    super("pen", "sprites/pen.png");
    this.getPen().down();
    this.getPen().setSize(2);
    this.getPen().setColor(Math.random() * 255);
    this.setOnEdgeBounce(true);
    this.hide();
  }
  
  // when I start as a clone
  public Pen(Pen pen) {
    super(pen);
    if (Math.random() < 0.50) {
      this.getPen().changeColor(Math.random() * 20); 
    }
  }
  
  public void setFinished() {
    this.finished = true;
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
