class MinuteHandSprite extends ScratchSprite {
  MinuteHandSprite() {
    super("hand", "sprites/minute.png");
  }

  void draw() {
    super.draw();
    int minute = this.getCurrentMinute();
    this.setRotation(minute / 60.0 * 360);
  }
}
