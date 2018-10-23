// import additional processing libraries
import processing.sound.*;
import eu.barkmin.processing.scratch.*;

ScratchStage stage;
ArrayList<Pen> pens = new ArrayList();

void setup() {
  size(800, 600);
  
  stage = ScratchStage.getInstance(this);
  stage.addSound("background", "sounds/bensound-enigmatic.wav");
  stage.playSound("background");
  
  Pen pen = new Pen();
  pen.setPosition(width / 2, height / 2);
  pens.add(pen);
}

void draw() {
  stage.draw();
  for(int i = 0; i < pens.size(); i++) {
    Pen pen = pens.get(i);
    pen.draw();
    
    if(Math.random() < 0.05 && pens.size() < 25) {
      pens.add(new Pen(pen));
    }
    if(Math.random() < 0.01 && pens.size() > 1) {
      pens.remove(i);
    }
  }
}
