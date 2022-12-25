import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteIsTouchingSprite {
    public SpriteIsTouchingSprite() {
        Stage myStage = new Stage(254, 100);

        Sprite gamma = new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png");
        gamma.setPosition(0, 50);
        myStage.add(gamma);
        Sprite zeta = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        zeta.setPosition(254, 50);
        myStage.add(zeta);

        while(!gamma.isTouchingSprite(zeta)) {
            gamma.changeX(5);
            zeta.changeX(-5);
            myStage.wait(100);
        }
    }

    public static void main(String[] args) {
        new SpriteIsTouchingSprite();
    }
}

