import eu.barkmin.processing.scratch.*;

ScratchStage stage;

VineSprite vine;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
  stage = ScratchStage.getInstance();
  stage.setColor(0, 0, 0);
  
  vine = new VineSprite();
  stage.addSprite(vine);
}

void draw() {}

class VineSprite extends ScratchSprite {
  
  ArrayList<LeafSprite> leafs = new ArrayList();
  
  VineSprite() {
    this.addCostume("vine", "sprites/vine.png");
    this.getPen().down();
    this.getPen().setSize(3);
    this.getPen().setColor(120);
    this.hide();
  }
  
  void run() {
    this.setPosition(ScratchStage.parent.mouseX, ScratchStage.parent.mouseY);
    this.turnRight(5);
    
    for(int i = 0; i < leafs.size(); i++) {
      leafs.get(i).draw();
    }
    
    if (stage.getTimer().everyMillis(20)) {
      leafs.add(new LeafSprite(vine));
    }
  }
}

class LeafSprite extends ScratchSprite {
  VineSprite vine;
  
  LeafSprite(VineSprite vine) {
    super("leaf", "sprites/vine.png");
    this.getPen().down();
    this.getPen().setSize(2);
    this.vine = vine;
    this.setRotation(vine.getRotation());
    // use current color from vine for pen
    this.getPen().setColor(vine.getPen().getColor());
    // update vine color with every leaf spawn
    vine.getPen().changeColor(2);
    this.setPosition((int) vine.getX(), (int) vine.getY());
    this.hide();
  }
  
  void draw() {
    super.draw();
    this.turnRight(5);
    this.move(10);
    
    // slowly increase pen size
    this.getPen().changeSize(1);
    
    // remove leaf after 200ms
    if (this.getTimer().everyMillis(200)) {
      this.vine.leafs.remove(this);
    }
  }
}
