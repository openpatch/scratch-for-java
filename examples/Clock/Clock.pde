import org.openpatch.scratch.*;

Stage stage;
ClockSprite clock;

void setup() {
  size(800, 800);
  stage = new Stage(this);
  clock = new ClockSprite();
  stage.add(clock);
}

void draw() {
}
