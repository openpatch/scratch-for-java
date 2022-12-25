import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteIsTouchingMousePointer {
    public SpriteIsTouchingMousePointer() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        mySprite.changeX(-100);
        mySprite.changeY(30);
        myStage.add(mySprite);

        while(true) {
            mySprite.say("Is touching mouse? " + mySprite.isTouchingMousePointer());
        }
    }

    public static void main(String[] args) {
        new SpriteIsTouchingMousePointer();
    }
}
