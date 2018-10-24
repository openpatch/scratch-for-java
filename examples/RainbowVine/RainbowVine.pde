import eu.barkmin.processing.scratch.*;

ScratchStage stage;

ArrayList<ScratchSprite> sprites = new ArrayList();
VineSprite vine;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
  stage = ScratchStage.getInstance();
  stage.setColor(0, 0, 0);
  vine = new VineSprite();
  sprites.add(vine);
}

void draw() {
  for(int i = 0; i < sprites.size(); i++) {
    sprites.get(i).draw();
  }
  sprites.add(new LeafSprite(vine));
}

class VineSprite extends ScratchSprite {
  VineSprite() {
    super("vine", "sprites/vine.png");
    this.getPen().down();
    this.getPen().setSize(3);
    this.hide();
  }
  
  void draw() {
    super.draw();
    this.setPosition(ScratchStage.parent.mouseX, ScratchStage.parent.mouseY);
    this.getPen().changeColor(10);
    this.turnRight(5);
  }
}

class LeafSprite extends ScratchSprite {
  int startMillis;
  VineSprite vine;
  
  LeafSprite(VineSprite vine) {
    super("leaf", "sprites/vine.png");
    this.getPen().down();
    this.getPen().setSize(2);
    this.startMillis = millis();
    this.vine = vine;
    this.setRotation(vine.getRotation());
    this.getPen().setColor(vine.getPen().getColor());
    this.setPosition((int) vine.getX(), (int) vine.getY());
    this.hide();
  }
  
  void draw() {
    super.draw();
    this.turnRight(5);
    this.move(10);
    this.getPen().changeSize(1);
    if ((millis() - this.startMillis) / 1000.0 >= 0.2) {
      sprites.remove(this);
    }
  }
}
