import org.openpatch.scratch.*;

public class SpriteAnimation {
    public SpriteAnimation() {
        Stage myStage = new Stage(254, 100);
        AnimatedSprite bee = new AnimatedSprite();
        bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
        myStage.add(bee);

        while(true) {
            bee.playAnimation("idle");
        }
    }

    public static void main(String[] args) {
        new SpriteAnimation();
    }
}
