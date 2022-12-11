import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.Stage;

public class SpriteSheet {
    public static void main(String[] args) {
       Stage stage = new Stage() ;
       stage.add(new AnimatedBee());
    }
}

class AnimatedBee extends AnimatedSprite {
    public AnimatedBee() {
        this.addAnimation("idle", "bee_idle.png", 6, 36, 34);
    }

    public void run() {
        this.playAnimation("idle");
    }
}
