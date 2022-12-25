import org.openpatch.scratch.Stage;

public class StageGetCurrentBackdropIndex {
    public StageGetCurrentBackdropIndex() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.addBackdrop("sea", "examples/java/assets/background_sea.png");
        System.out.println(myStage.getCurrentBackdropIndex()); // 0
        myStage.nextBackdrop();
        System.out.println(myStage.getCurrentBackdropIndex()); // 1
    }

    public static void main(String[] args) {
        new StageGetCurrentBackdropIndex();
    }
}
