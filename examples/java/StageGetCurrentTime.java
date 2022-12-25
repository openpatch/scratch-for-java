import org.openpatch.scratch.Stage;

public class StageGetCurrentTime {
    public StageGetCurrentTime() {
        Stage myStage = new Stage(254, 100);
        int year = myStage.getCurrentYear();
        System.out.println("Year: " + year);
        int month = myStage.getCurrentMonth();
        System.out.println("Month: " + month);
        int day = myStage.getCurrentDay();
        System.out.println("Day: " + day);
        int weekday = myStage.getCurrentDayOfWeek();
        System.out.println("Weekday: " + weekday);
        int hour = myStage.getCurrentHour();
        System.out.println("Hour: " + hour);
        int minute = myStage.getCurrentMinute();
        System.out.println("Minute: " + minute);
        int second = myStage.getCurrentSecond();
        System.out.println("Second: " + second);
        int millisecond = myStage.getCurrentMillisecond();
        System.out.println("Millisecond: " + millisecond);
        int daysSince2000 = myStage.getDaysSince2000();
        System.out.println("Days since 2000: " + daysSince2000);
    }

    public static void main(String[] args) {
        new StageGetCurrentTime();
    }
}
