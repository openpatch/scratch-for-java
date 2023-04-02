import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class CatSketch {
    class CatSprite extends Sprite {
        CatSprite() {
            this.addCostume("cat", "sprites/cat.png");
            this.setOnEdgeBounce(true);
        }

        @Override
        public void run() {
            this.move(2);
            this.think("Hallo wie geht es dir?");
        }
    }

    public CatSketch() {
        Stage myStage = new Stage(800, 600);
        Sprite myCat = new CatSprite();
        myStage.add(myCat);
    }

    public static void main(String[] args) {
        new CatSketch();
    }
}
