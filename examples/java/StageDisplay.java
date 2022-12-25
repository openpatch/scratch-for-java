import org.openpatch.scratch.Stage;

public class StageDisplay {
    public StageDisplay() {
        Stage myStage = new Stage(254, 100);
        myStage.display("Welcome! This is a longer text with auto line breaks!", 2000);
    }

    public static void main(String[] args) {
        new StageDisplay();
    }
}
