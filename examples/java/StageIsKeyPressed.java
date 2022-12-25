import org.openpatch.scratch.Stage;

public class StageIsKeyPressed {
    public StageIsKeyPressed() {
        Stage myStage = new Stage(254, 100);

        while(true) {
            myStage.display("Space pressed? " + myStage.isKeyPressed(32));
        }
    }

    public static void main(String[] args) {
        new StageIsKeyPressed();
    }
}
