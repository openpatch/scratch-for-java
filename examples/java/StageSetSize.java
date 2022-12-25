import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageSetSize {
    public StageSetSize() {
        Stage myStage = new Stage(254, 100);
        Sprite gamma = new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png");
        myStage.add(gamma);
        myStage.wait(3000);
        myStage.setSize(500, 200);
    }
    public static void main(String[] args) {
        new StageSetSize();
    }
    
}
