class Wheel extends ScratchSprite {

  WheelPart[] parts;


  int currentColorIndex;

  Wheel(int parts) {
    this.parts = new WheelPart[parts];
    
    currentColorIndex = 1;

    for (int i = 0; i < this.parts.length; i++) {
      this.parts[i] = new WheelPart(parts);
      this.parts[i].setRotation(i * 360 / this.parts.length);
      this.parts[i].setTint(colors[i % colors.length]);
    }

    this.setPosition(width / 2, 150);
  }
  
  void rotate(int degrees) {
    this.turnLeft(degrees);
    for (WheelPart part : parts) {
      part.turnLeft(degrees);
    }
  }

  void draw() {
    super.draw();

    // check which color is currently at the bottom
    for(int i = 0; i < this.parts.length; i++) {
      float limit = 360 - i * 360.0 / this.parts.length;
      if (this.getRotation() < limit) {
        currentColorIndex =  i % colors.length;
      }
    }

    for (WheelPart part : parts) {
      part.setPosition(this.getX(), this.getY());
      part.draw();
    }
  }
}
