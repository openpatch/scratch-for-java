import org.openpatch.scratch.Stage;

class CustomStage extends Stage {
    public CustomStage() {
        super(254,100);
    }
    @Override
    public void whenKeyPressed(int keyCode) {
        this.display("Key Pressed: " + keyCode);
    }
}

public class StageWhenKeyPressed {
    public StageWhenKeyPressed() {
        new CustomStage();
    }

    public static void main(String[] args) {
        new StageWhenKeyPressed();
    }
}
