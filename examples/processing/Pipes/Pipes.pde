// import additional processing libraries
import processing.sound.*;
import org.openpatch.scratch.*;

Stage stage;
ArrayList<PenSprite> pens = new ArrayList();

void setup() {
  size(1280, 800);
  stage = new Stage(this);
  
  stage.addBackdrop("chalkBoard", "backdrops/chalk_board.jpg");
  stage.setTint(60);
  
  PenSprite pen = new PenSprite();
  pens.add(pen);
}

void draw() {
  for(int i = 0; i < pens.size(); i++) {
    PenSprite pen = pens.get(i);
    
    if(Math.random() < 0.05 && pens.size() < 15) {
      pens.add(new PenSprite(pen));
    }
    if(Math.random() < 0.01 && pens.size() > 1) {
      pens.remove(i);
    }
  }
}

void keyPressed() {
  if (this.keyCode == 32) {
    PenSprite.setColor((float) Math.random() * 255);
  }
}
