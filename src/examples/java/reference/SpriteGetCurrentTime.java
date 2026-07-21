package reference;
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
      int year = Clock.getYear();
      int month = Clock.getMonth();
      int day = Clock.getDay();
      int weekday = Clock.getDayOfWeek();
      int hour = Clock.getHour();
      int minute = Clock.getMinute();
      int second = Clock.getSecond();
      int millisecond = Clock.getMillisecond();
      int daysSince2000 = Clock.getDaysSince2000();
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
