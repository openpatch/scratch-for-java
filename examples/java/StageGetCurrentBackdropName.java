import org.openpatch.scratch.Stage;

public class StageGetCurrentBackdropName {
    public StageGetCurrentBackdropName() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.addBackdrop("sea", "examples/java/assets/background_sea.png");
        System.out.println(myStage.getCurrentBackdropName()); // forest
        myStage.nextBackdrop();
        System.out.println(myStage.getCurrentBackdropName()); // sea
    }

    public static void main(String[] args) {
        new StageGetCurrentBackdropName();
    }
}
