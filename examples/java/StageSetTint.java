import org.openpatch.scratch.Stage;

public class StageSetTint {
    public StageSetTint() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.wait(2500);
        myStage.setTint(200);
    }

    public static void main(String[] args) {
        new StageSetTint();
    }
}