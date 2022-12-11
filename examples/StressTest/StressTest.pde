import processing.sound.*;
import org.openpatch.scratch.*;


Stage stage;

int dinos = 15;
int knights = 15;
int ninjas = 15;

ArrayList<Character> characters = new ArrayList();

void setup() {
  size(1000, 1000, P2D);
  stage = new Stage(this);

  stage.addBackdrop("outback", "assets/outback.png");
  stage.addSound("outback", "assets/outback.wav");
  
  for(int i = 0; i < dinos; i++) {
    characters.add(new Dino());
  }
  
  for(int i = 0; i < knights; i++) {
    characters.add(new Knight());
  }
  
  for(int i = 0; i < ninjas; i++) {
    characters.add(new Ninja());
  }
  
  for(Character c : characters) {
    stage.add(c);
  }
}

void draw() {
  stage.playSound("outback");
  text(frameRate, 20, 20);
}

void keyPressed() {
  if(keyCode == 32) {
   for(Character character : characters) {
      character.state = CharacterState.RUN;
    }
  }
}
