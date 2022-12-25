import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageAdd {
    public StageAdd() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new Sprite("cat", "docs/en/public/assets/logo.png"));
    }

    public static void main(String[] args) {
        new StageAdd();
    }
}
