import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteWhenClicked {

    class CustomSprite extends Sprite {
        public CustomSprite() {
            this.addCostume("zeta", "examples/java/assets/zeta_green_badge.png");
            this.addCostume("gamma", "examples/java/assets/gamma_purple_badge.png");
        }

        @Override
        public void whenClicked() {
            this.nextCostume();
        }
    }

    public SpriteWhenClicked() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new CustomSprite());
    }

    public static void main(String[] args) {
        new SpriteWhenClicked();
    }
}