class SecondHandSprite extends Sprite {

  SecondHandSprite() {
    this.addCostume("hand", "sprites/second.png");
  }

  void run() {
    int second = this.getCurrentSecond();
    if (isKeyPressed(32)) {
      int millisecond = this.getCurrentMillisecond();
      this.setRotation((second + millisecond / 1000.0) / 60.0 * 360);
    } else {
      this.setRotation(second / 60.0 * 360);
    }
  }
}
