import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetPosition {
    public SpriteSetPosition() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "examples/java/assets/slime.png");
        myStage.add(mySprite);

        while(true) {
            int x = myStage.pickRandom(0, myStage.getWidth());
            int y = myStage.pickRandom(0, myStage.getHeight());

            mySprite.setPosition(x, y);
            myStage.wait(100);
        }
        
    }

    public static void main(String[] args) {
        new SpriteSetPosition();
    }
}