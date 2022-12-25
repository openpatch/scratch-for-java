import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteWhenMouseMoved {

    class CustomSprite extends Sprite {
        public CustomSprite() {
            this.addCostume("zeta", "examples/java/assets/zeta_green_badge.png");
            this.addCostume("gamma", "examples/java/assets/gamma_purple_badge.png");
        }

        @Override
        public void whenMouseMoved(float x, float y) {
            this.setPosition(x, y);
        }
    }

    public SpriteWhenMouseMoved() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new CustomSprite());
    }

    public static void main(String[] args) {
        new SpriteWhenMouseMoved();
    }
}
