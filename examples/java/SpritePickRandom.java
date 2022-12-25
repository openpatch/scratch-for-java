import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpritePickRandom {
    public SpritePickRandom() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "examples/java/assets/zeta_green_badge.png");
        zeta.changeY(40);
        myStage.add(zeta);

        while(true) {
            int random = zeta.pickRandom(0, 100);
            zeta.think("Random: " + random);
            myStage.wait(500);
        }
    }
    public static void main(String[] args) {
        new SpritePickRandom();
    }
}
