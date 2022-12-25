import org.openpatch.scratch.Stage;

public class StageNextBackdrop {
    public StageNextBackdrop() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.addBackdrop("sea", "examples/java/assets/background_sea.png");
        
        myStage.wait(3000);
        myStage.nextBackdrop();
    }

    public static void main(String[] args) {
        new StageNextBackdrop();
    }
}
