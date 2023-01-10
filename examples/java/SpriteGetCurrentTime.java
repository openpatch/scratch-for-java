import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteGetCurrentTime {
    public SpriteGetCurrentTime() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
        mySprite.changeY(70);
        mySprite.setX(50);
        myStage.add(mySprite);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
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
            mySprite.say(hour + ":" + minute + ":" + second + ":" + millisecond + " " + year + "-" + month + "-" + day + " (" + weekday + ")" + "\n" + daysSince2000);
        }

        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new SpriteGetCurrentTime();
    }
}
