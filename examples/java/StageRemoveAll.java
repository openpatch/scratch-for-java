import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageRemoveAll {
    public StageRemoveAll() {
        Stage myStage = new Stage(254,100);
        new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png");
        myStage.add(new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png"));
        myStage.add(new Sprite("zeta", "examples/java/assets/zeta_green_badge.png"));
        myStage.wait(3000);
        myStage.removeAll();

    }

    public static void main(String[] args) {
        new StageRemoveAll();
    }
}
