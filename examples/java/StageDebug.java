import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.Stage;

public class StageDebug {
    public StageDebug() {
        Stage myStage = new Stage(254, 100);
        myStage.setDebug(true);
        AnimatedSprite bee = new AnimatedSprite();
        bee.addAnimation("idle", "assets/bee_idle.png", 6, 36, 34);
        bee.setRotation(45);
        myStage.add(bee);
    } 

    public static void main(String[] args) {
       new StageDebug(); 
    }
}
