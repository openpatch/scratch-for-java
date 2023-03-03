import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class StressTest extends Stage {

    private static int dinos = 80;
    private static int knights = 80;
    private static int ninjas = 80;

    public StressTest() {
        this.addBackdrop("outback", "assets/outback.png");
        this.addSound("outback", "assets/outback.wav");

        for (int i = 0; i < dinos; i++) {
            this.add(new Dino());
        }
        for (int i = 0; i < knights; i++) {
            this.add(new Knight());
        }
        for (int i = 0; i < ninjas; i++) {
            this.add(new Ninja());
        }
    }

    public void run() {
        this.playSound("outback");
    }

    public void whenKeyPressed(int keyCode) {
        if (keyCode == KeyCode.VK_SPACE) {
            var characters = this.find(Character.class);
            for (var character : characters) {
                ((Character) character).state = CharacterState.RUN;
            }
        }
    }

    public static void main(String[] args) {
        Window myWindow = new Window(800, 800, "assets");
        myWindow.addStage("main", new StressTest());
    }

}
