import org.openpatch.scratch.Stage;

public class StageSound {
    public StageSound() {
        Stage myStage = new Stage(254, 100);
        myStage.addSound("background", "assets/music.mp3");
        myStage.playSound("background");
    }
    public static void main(String[] args) {
        new StageSound();
    }
}
