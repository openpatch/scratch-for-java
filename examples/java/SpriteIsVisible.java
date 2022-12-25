import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteIsVisible {
    public SpriteIsVisible() {
        Stage myStage = new Stage(254, 100);
        Sprite gamma = new Sprite("gamma", "examples/java/assets/gamma_purple_badge.png");
        myStage.add(gamma);
        myStage.display("Gamma Visible? " + gamma.isVisible());
        myStage.wait(3000);
        gamma.hide();
        myStage.display("Gamma Visible? " + gamma.isVisible());
    }    

    public static void main(String[] args) {
        new SpriteIsVisible();
    }
}
