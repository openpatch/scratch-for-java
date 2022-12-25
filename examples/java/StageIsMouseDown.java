import org.openpatch.scratch.Stage;

public class StageIsMouseDown {
    public StageIsMouseDown() {
        Stage myStage = new Stage(254, 100);

        while(true) {
            myStage.display("Mouse down? " + myStage.isMouseDown());
        }
    }

    public static void main(String[] args) {
        new StageIsMouseDown();
    }
}
