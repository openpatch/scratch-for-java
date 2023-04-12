import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.*;
import org.openpatch.scratch.Window;

public class StageGetCurrentTime {
    public StageGetCurrentTime() {
        Stage myStage = new Stage(254, 100);

        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while (myStage.getTimer().forMillis(3000)) {
            int year = myStage.getCurrentYear();
            int month = myStage.getCurrentMonth();
            int day = myStage.getCurrentDay();
            int weekday = myStage.getCurrentDayOfWeek();
            int hour = myStage.getCurrentHour();
            int minute = myStage.getCurrentMinute();
            int second = myStage.getCurrentSecond();
            int millisecond = myStage.getCurrentMillisecond();
            int daysSince2000 = myStage.getDaysSince2000();
            myStage.display(hour + ":" + minute + ":" + second + ":" + millisecond + " " + year + "-" + month + "-" + day + " (" + weekday + ")" + " " + daysSince2000);
        }
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new StageGetCurrentTime();
    }
}
