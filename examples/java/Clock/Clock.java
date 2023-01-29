import org.openpatch.scratch.Stage;

public class Clock {
    public Clock() {
        Stage myStage = new Stage(800, 800);
        myStage.add(new ClockSprite());
    }

    public static void main(String[] args) {
        new Clock();
    }
}
