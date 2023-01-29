import org.openpatch.scratch.Stage;

public class Robot extends Stage {
    public Robot() {
        super(800, 600);
        this.add(new RobotSprite());
    }

    public static void main(String[] args) {
        new Robot();
    }
    
}
