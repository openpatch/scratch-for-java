import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.Stage;

public class SpriteSheetRows {
    public static void main(String[] args) {
       Stage stage = new Stage() ;
       stage.add(new AnimatedFrog());
    }
}


class AnimatedFrog extends AnimatedSprite {
    public AnimatedFrog() {
        this.addAnimation("jump", "frog_spritesheet.png", 4, 64, 64);
    }

    public void run() {
        this.playAnimation("jump");
    }
}
