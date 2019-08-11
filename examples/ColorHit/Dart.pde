class Dart extends ScratchSprite {

  DartFlight flight;
  DartShaft shaft;
  
  int flightColor = 255;

  int currentColorIndex;

  Dart() {
    flight = new DartFlight();
    shaft = new DartShaft();
    this.setSize(50);
    this.setRotation(-90);
  }

  void newColor() {
    currentColorIndex = (int) random(0, colors.length);
    flightColor = colors[currentColorIndex];
  }

  void draw() {
    this.getX();
    flight.setPosition(this.getX(), this.getY() + 60 * this.getSize() / 100.0);
    flight.setSize(this.getSize());
    flight.setTint(flightColor);
    flight.draw();
    shaft.setPosition(this.getX(), this.getY() - 10 * this.getSize() / 100.0);
    shaft.setSize(this.getSize());
    shaft.draw();
  }
}
