import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteShow {
   public SpriteShow() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "examples/java/assets/slime.png");
        myStage.add(mySprite);
        myStage.wait(3000);
        mySprite.hide();
        myStage.wait(3000);
        mySprite.show();
   } 

   public static void main(String[] args) {
    new SpriteShow();
   }
}
