class WheelPart extends ScratchSprite {
  WheelPart(int parts) {
    super("full", "sprites/full.png");
    this.addCostume("half", "sprites/half.png");
    this.addCostume("quarter", "sprites/quarter.png");
    this.addCostume("eighth", "sprites/eighth.png");
    this.setSize(50);

    switch (parts) {
    case 1:
      this.switchCostume("full");
      break;
    case 2:
      this.switchCostume("half");
      break;
    case 4:
      this.switchCostume("quarter");
      break;
    case 8:
      this.switchCostume("eighth");
      break;
    }
  }
}
