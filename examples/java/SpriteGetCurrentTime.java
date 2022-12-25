import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteGetCurrentTime {
    public SpriteGetCurrentTime() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        mySprite.changeY(70);
        mySprite.setX(50);
        myStage.add(mySprite);

        while (true) {
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
    }

    public static void main(String[] args) {
        new SpriteGetCurrentTime();
    }
}
