import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageLower {
    public StageLower() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new Sprite("zeta", "examples/java/assets/zeta_green_badge.png"));
        Sprite gamma = new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png");
        myStage.add(gamma);

        myStage.wait(3000);
        myStage.lower(gamma);

    }

    public static void main(String[] args) {
        new StageLower();
    }
}
