class MinuteHandSprite extends ScratchSprite {
  MinuteHandSprite() {
    this.addCostume("hand", "sprites/minute.png");
  }

  void run() {
    int minute = this.getCurrentMinute();
    this.setRotation(minute / 60.0 * 360);
  }
}
