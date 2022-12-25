import org.openpatch.scratch.Stage;

public class StageChangeTint {
    public StageChangeTint() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.wait(2500);
        myStage.changeTint(200);
    }

    public static void main(String[] args) {
        new StageChangeTint();
    }
}
