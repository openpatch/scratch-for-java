import eu.barkmin.processing.scratch.*;

ScratchStage stage;
Wheel wheel;
Dart dart;
ArrayList<Dart> dartLifes;

int[] colors = {50, 180};
int level = 1;
int score = 0;
int lifes = 3;
int speed = 1;

enum States {
  THROW, HIT, INIT, LOST
}
States state = States.INIT;

void setup() {
  size(400, 600, P2D);
  ScratchStage.init(this);
  stage = ScratchStage.getInstance();
  stage.addBackdrop("bg", "sprites/bg.png");
  stage.setTint(120);
  wheel = new Wheel(8);
  dart = new Dart();
  dart.setPosition(width / 2, height - 100);
  dart.newColor();
  updateLifes();
}

void updateLifes() {  
  dartLifes = new ArrayList();
  for(int i = 0; i < lifes; i++) {
    Dart life = new Dart();
    life.setSize(20);
    life.setPosition(20 + i * 20, height - 40);
    dartLifes.add(life);
  }
}

void checkDart() {
  if (dart.currentColorIndex == wheel.currentColorIndex) {
    score++;
  } else {
    lifes--;
    updateLifes();
  }

  if (lifes < 0) {
    state = States.LOST;
    return;
  }
  
  if(score > level * 10) {
    speed++;
    level++;
    lifes++;
    updateLifes();
  }
  
  // next dart
  dart.newColor();
  state = States.INIT;
  dart.setPosition(width / 2, height - 100);
}

void keyPressed() {
  if (keyCode == 32 && state == States.INIT) { 
    state = States.THROW;
  }
  if (keyCode == ENTER && state == States.LOST) {
    state = States.INIT;
    score = 0;
    lifes = 3;
    speed = 1;
    dart.setPosition(width / 2, height - 100);
  }
}

void draw() {
  // draw score
  fill(255);
  text(score, width - 40, height - 20);
  
  // draw lifes
  for(Dart life : dartLifes) {
      life.draw();
  }

  // draw wheel and dart depending on state
  switch (state) {
  case INIT:
    dart.draw();
    wheel.draw();
    wheel.rotate(speed);
    break;
  case THROW:
    dart.draw();
    wheel.draw();
    wheel.rotate(speed);
    dart.move(10);
    break;
  case HIT:
    checkDart();
    dart.draw();
    wheel.draw();
    delay(1000);
    break;
  case LOST:
    background(0);
    textAlign(CENTER);
    text("You scored " + score + " Points! Great try!\n Press Enter to play again.", width / 2, height / 2);
    break;
  }

  // change state if dart has hit wheel
  if (dart.getY() <= 300 && state != States.LOST) {
    state = States.HIT;
  }
}
