import org.openpatch.scratch.Stage;

public class StageGetMouse {
    public StageGetMouse() {
        Stage myStage = new Stage(254,100);
        
        while(true) {
            float mouseX = myStage.getMouseX();
            float mouseY = myStage.getMouseY();

            myStage.display("X: " + mouseX + " Y: " + mouseY);
        }
    }

    public static void main(String[] args) {
        new StageGetMouse();
    }
}
