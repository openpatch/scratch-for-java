class SecondHandSprite extends ScratchSprite {

  SecondHandSprite() {
    super("hand", "sprites/second.png");
  }

  void draw() {
    super.draw();
    int second = this.getCurrentSecond();
    if (isKeyPressed(32)) {
      int millisecond = this.getCurrentMillisecond();
      this.setRotation((second + millisecond / 1000.0) / 60.0 * 360);
    } else {
      this.setRotation(second / 60.0 * 360);
    }
  }
}
