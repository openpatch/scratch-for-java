import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageSetSize {
    public StageSetSize() {
        Stage myStage = new Stage(254, 100);
        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        myStage.add(gamma);
        myStage.wait(2000);
        myStage.setSize(500, 200);
        myStage.wait(2000);
        System.exit(0);
    }
    public static void main(String[] args) {
        new StageSetSize();
    }
    
}
