import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteNextCostume {
    public SpriteNextCostume() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "examples/java/assets/zeta_green_badge.png");
        zeta.addCostume("yellow", "examples/java/assets/zeta_yellow_badge.png");
        myStage.add(zeta);

        while(true) {
            myStage.wait(3000);
            zeta.nextCostume();
        }
    }
    public static void main(String[] args) {
        new SpriteNextCostume();
    }
}
