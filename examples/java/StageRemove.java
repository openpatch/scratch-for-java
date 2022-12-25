import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageRemove {
    public StageRemove() {
        Stage myStage = new Stage(254,100);
        Sprite gamma = new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png");
        myStage.add(gamma);
        myStage.wait(3000);
        myStage.remove(gamma);
    }

    public static void main(String[] args) {
        new StageRemove();
    }
}
