import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageRaise {
    public StageRaise() {
        Stage myStage = new Stage(254, 100);

        Sprite gamma = new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png");
        myStage.add(gamma);

        myStage.add(new Sprite("zeta", "examples/java/assets/zeta_green_badge.png"));

        myStage.wait(3000);
        myStage.raise(gamma);

    }

    public static void main(String[] args) {
        new StageRaise();
    }
}
