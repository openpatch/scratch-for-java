import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteGetMouse {
    public SpriteGetMouse() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        mySprite.changeY(30);
        mySprite.changeX(-100);
        myStage.add(mySprite);
        while (true) {
            mySprite.say("X: " + mySprite.getMouseX() + " Y: " + mySprite.getMouseY());
        }
    }

    public static void main(String[] args) {
        new SpriteGetMouse();
    }
}
