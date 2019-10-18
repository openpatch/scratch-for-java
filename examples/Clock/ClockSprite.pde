class ClockSprite extends ScratchSprite {

  SecondHandSprite second;
  MinuteHandSprite minute;
  HourHandSprite hour;

  ClockSprite() {
    this.addCostume("clock", "sprites/clock.png");
    second = new SecondHandSprite();
    minute = new MinuteHandSprite();
    hour = new HourHandSprite();
  }

  void run() {
    second.draw();
    minute.draw();
    hour.draw();
  }
}
