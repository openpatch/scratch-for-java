import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.Pen;

public class StageFind {
    public StageFind() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new Pen());
        myStage.add(new Pen());
        myStage.add(new Sprite());

        System.out.println(myStage.find(Sprite.class).size()); // 1
        System.out.println(myStage.find(Pen.class).size()); // 2
    }

    public static void main(String[] args) {
        new StageFind();
    }
}
