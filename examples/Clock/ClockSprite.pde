class ClockSprite extends ScratchSprite {

  SecondHandSprite second;
  MinuteHandSprite minute;
  HourHandSprite hour;

  ClockSprite() {
    super("clock", "sprites/clock.png");
    second = new SecondHandSprite();
    minute = new MinuteHandSprite();
    hour = new HourHandSprite();
  }

  void draw() {
    second.draw();
    minute.draw();
    hour.draw();
    super.draw();
  }
}
