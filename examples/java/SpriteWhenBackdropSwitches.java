import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteWhenBackdropSwitches {
    class CustomSprite extends Sprite {
        public CustomSprite() {
            this.addCostume("zeta", "examples/java/assets/zeta_green_badge.png");
            this.addCostume("gamma", "examples/java/assets/gamma_purple_badge.png");
        }

        @Override
        public void whenBackdropSwitches(String name) {
            if (name.equals("forest")) {
                this.switchCostume("zeta");
            } else if (name.equals("sea")) {
                this.switchCostume("gamma");
            }
        }
    }

    public SpriteWhenBackdropSwitches() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.addBackdrop("sea", "examples/java/assets/background_sea.png");
        myStage.add(new CustomSprite());

        while (true) {
            if (myStage.getTimer().intervalMillis(2000)) {
                myStage.switchBackdrop("sea");
            } else {
                myStage.switchBackdrop("forest");
            }
        }
    }

    public static void main(String[] args) {
        new SpriteWhenBackdropSwitches();
    }
}