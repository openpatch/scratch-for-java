import org.openpatch.scratch.Stage;

public class StageSetTransparency {
    public StageSetTransparency() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.wait(2500);
        myStage.setTransparency(50);;
    }

    public static void main(String[] args) {
        new StageSetTransparency();
    }
}
