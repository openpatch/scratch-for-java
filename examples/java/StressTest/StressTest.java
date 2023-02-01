import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Renderer;
import org.openpatch.scratch.Stage;

public class StressTest extends Stage {

    private static int dinos = 15;
    private static int knights = 15;
    private static int ninjas = 15;
    public StressTest() {
        super(1000, 1000, Renderer.OPENGL);

        this.addBackdrop("outback", "assets/outback.png");
        this.addSound("outback", "assets/outback.wav");

        for (int i = 0; i<dinos; i++) {
            this.add(new Dino());
        }
        for (int i = 0; i<knights; i++) {
            this.add(new Knight());
        }
        for (int i = 0; i<ninjas; i++) {
            this.add(new Ninja());
        }
    }

    public void run() {
        this.playSound("outback");
        this.display("Frame Rate: " + this.getFrameRate());
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
        new StressTest();
    }

}
