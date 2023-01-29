class MinuteHandSprite extends Sprite {
  MinuteHandSprite() {
    this.addCostume("hand", "sprites/minute.png");
  }

  void run() {
    int minute = this.getCurrentMinute();
    this.setDirection(minute / 60.0 * 360);
  }
}
