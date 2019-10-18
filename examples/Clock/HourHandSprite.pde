class HourHandSprite extends ScratchSprite {
  HourHandSprite() {
    this.addCostume("hand", "sprites/hour.png");
  }

  void run() {
    int hour = this.getCurrentHour();
    this.setRotation(hour / 12.0 * 360);
  }
}
