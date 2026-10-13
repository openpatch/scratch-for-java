package demos.clock;

import org.openpatch.scratch.*;

public class ClockStage extends Stage {

  // begin stage designer
  private ClockSprite clockSprite;
  private SecondHandSprite secondHandSprite;
  private MinuteHandSprite minuteHandSprite;
  private HourHandSprite hourHandSprite;
  // end stage designer

  public ClockStage() {
    // begin stage designer
    clockSprite = new ClockSprite();
    this.add(clockSprite);
    secondHandSprite = new SecondHandSprite();
    this.add(secondHandSprite);
    minuteHandSprite = new MinuteHandSprite();
    this.add(minuteHandSprite);
    hourHandSprite = new HourHandSprite();
    this.add(hourHandSprite);
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(800, 800);
    myWindow.setStage(new ClockStage());
  }
}
