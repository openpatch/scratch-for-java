import org.openpatch.scratch.*;

public class Target extends Sprite {

    public Target() {
        this.addCostume("target", "assets/target.png");
        this.setHitbox(10, 38, 10, 10, 38, 10, 38, 38);
    }
}