class HourHandSprite extends ScratchSprite {
  HourHandSprite() {
    super("hand", "sprites/hour.png");
  }

  void draw() {
    super.draw();
    int hour = this.getCurrentHour();
    this.setRotation(hour / 12.0 * 360);
  }
}
