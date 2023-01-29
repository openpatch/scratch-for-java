import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class ClockSprite extends Sprite {

  SecondHandSprite second;
  MinuteHandSprite minute;
  HourHandSprite hour;

  public ClockSprite() {
    this.addCostume("clock", "sprites/clock.png");
    second = new SecondHandSprite();
    minute = new MinuteHandSprite();
    hour = new HourHandSprite();
    Stage.getInstance().add(second);
    Stage.getInstance().add(minute);
    Stage.getInstance().add(hour);
  }
}

