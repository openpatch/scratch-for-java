package demos.clock;

import org.openpatch.scratch.*;

public class ClockStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private ClockSprite clockSprite;
  private SecondHandSprite secondHandSprite;
  private MinuteHandSprite minuteHandSprite;
  private HourHandSprite hourHandSprite;
  // scratch4j:end fields

  public ClockStage() {
    super(800, 800);
    // scratch4j:begin setup (managed by the stage designer)
    clockSprite = new ClockSprite();
    this.add(clockSprite);
    secondHandSprite = new SecondHandSprite();
    this.add(secondHandSprite);
    minuteHandSprite = new MinuteHandSprite();
    this.add(minuteHandSprite);
    hourHandSprite = new HourHandSprite();
    this.add(hourHandSprite);
    // scratch4j:end setup
  }

  public static void main(String[] args) {
    new ClockStage();
  }
}
