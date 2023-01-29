import org.openpatch.scratch.AnimatedSprite;
import org.openpatch.scratch.Stage;

public class Character extends AnimatedSprite {
    public CharacterState state;
    int tintColor;
    boolean hasTouchedEdge = false;

    public Character(String pathBase, int idleAnimations, int runAnimations, int walkAnimations) {
        // String pathBase = "assets/" + name + "/";
        super();
        state = CharacterState.IDLE;

        this.addSound("bump", "assets/bump.wav");
        this.addSound("run", "assets/run.wav");

        this.addAnimation("idle", pathBase + "Idle (%d).png", idleAnimations);
        this.addAnimation("run", pathBase + "Run (%d).png", runAnimations);
        this.addAnimation("walk", pathBase + "Walk (%d).png", walkAnimations);

        this.setOnEdgeBounce(true);
        this.setSize(30);

        this.tintColor = (int) this.pickRandom(0, 256);
        this.setPosition(this.pickRandom(200, Stage.getInstance().getWidth() - 200),
                this.pickRandom(200, Stage.getInstance().getHeight() - 200));
        this.setDirection(this.pickRandom(0, 360));
    }

    public void run() {
        this.setTint(this.tintColor);

        if (isTouchingMousePointer()) {
            state = CharacterState.WALK;
        } else if (state == CharacterState.WALK) {
            state = CharacterState.IDLE;
        }

        if (isTouchingEdge() && !hasTouchedEdge) {
            this.playSound("bump");
            hasTouchedEdge = true;
        } else if (!isTouchingEdge() && hasTouchedEdge) {
            hasTouchedEdge = false;
        }

        switch (state) {
            case IDLE:
                this.setAnimationInterval(100);
                this.playAnimation("idle");
                break;
            case RUN:
                this.setAnimationInterval(50);
                this.playAnimation("run");
                move(4);
                break;
            case WALK:
                this.setAnimationInterval(100);
                this.playAnimation("walk");
                move(2);
                break;
        }
    }
}
