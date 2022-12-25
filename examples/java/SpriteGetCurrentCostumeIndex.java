import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteGetCurrentCostumeIndex {
    public SpriteGetCurrentCostumeIndex() {
        Stage myStage = new Stage(256, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        mySprite.addCostume("gamma", "examples/java/assets/gamma_purple_badge.png");
        mySprite.changeY(20);
        myStage.add(mySprite);
        mySprite.think("Index: " + mySprite.getCurrentCostumeIndex());
        myStage.wait(2000);
        mySprite.nextCostume();
        mySprite.think("Index: " + mySprite.getCurrentCostumeIndex());
    }

    public static void main(String[] args) {
        new SpriteGetCurrentCostumeIndex();
    }
}
