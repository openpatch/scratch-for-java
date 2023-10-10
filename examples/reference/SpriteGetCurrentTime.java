import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteGetCurrentTime {
  public SpriteGetCurrentTime() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    myStage.add(mySprite);
    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();

    while (myStage.getTimer().forMillis(3000)) {
      int year = mySprite.getCurrentYear();
      int month = mySprite.getCurrentMonth();
      int day = mySprite.getCurrentDay();
      int weekday = mySprite.getCurrentDayOfWeek();
      int hour = mySprite.getCurrentHour();
      int minute = mySprite.getCurrentMinute();
      int second = mySprite.getCurrentSecond();
      int millisecond = mySprite.getCurrentMillisecond();
      int daysSince2000 = mySprite.getDaysSince2000();
      mySprite.say(
          hour
              + ":"
              + minute
              + ":"
              + second
              + ":"
              + millisecond
              + " "
              + year
              + "-"
              + month
              + "-"
              + day
              + " ("
              + weekday
              + ")"
              + "\n"
              + daysSince2000);
    }

    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteGetCurrentTime();
  }
}
