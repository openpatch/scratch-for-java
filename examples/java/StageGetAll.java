import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.Pen;

public class StageGetAll {
    public StageGetAll() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new Pen());
        myStage.add(new Pen());
        myStage.add(new Sprite());

        System.out.println(myStage.getAll().size()); // 3
    }

    public static void main(String[] args) {
        new StageGetAll();
    }
}
