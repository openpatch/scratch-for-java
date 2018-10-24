// import additional processing libraries
import processing.sound.*;
import eu.barkmin.processing.scratch.*;

ScratchStage stage;
ArrayList<PenSprite> pens = new ArrayList();

void setup() {
  size(1280, 800);
  ScratchStage.init(this);
  
  stage = ScratchStage.getInstance();
  stage.addSound("background", "sounds/bensound-enigmatic.wav");
  stage.playSound("background");
  stage.addBackdrop("chalkBoard", "backdrops/chalk_board.jpg");
  stage.setTint(60);
  
  PenSprite pen = new PenSprite();
  pens.add(pen);
}

void draw() {
  for(int i = 0; i < pens.size(); i++) {
    PenSprite pen = pens.get(i);
    pen.draw();
    
    if(Math.random() < 0.05 && pens.size() < 15) {
      pens.add(new PenSprite(pen));
    }
    if(Math.random() < 0.01 && pens.size() > 1) {
      pens.remove(i);
    }
  }
}

void keyPressed() {
  for (PenSprite pen : pens) {
    pen.keyPressed(this.keyCode);
  }
  
  if (this.keyCode == 32) {
    PenSprite.setColor((float) Math.random() * 255);
  }
}
