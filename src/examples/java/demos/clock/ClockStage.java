package demos.clock;

import org.openpatch.scratch.*;

public class ClockStage extends Stage {
  public ClockStage() {
    super(800, 800);
    this.add(new ClockSprite());
    this.add(new SecondHandSprite());
    this.add(new MinuteHandSprite());
    this.add(new HourHandSprite());
  }

  public static void main(String[] args) {
    new ClockStage();
  }
}
