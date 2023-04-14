import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.animation.*;

public class Character extends AnimatedSprite {
  public CharacterState state;
  int tintColor;
  boolean hasTouchedEdge = false;

  public Character(String pathBase, int idleAnimations, int runAnimations, int walkAnimations) {
    state = CharacterState.IDLE;

    this.addAnimation("idle", pathBase + "Idle (%d).png", idleAnimations);
    this.addAnimation("run", pathBase + "Run (%d).png", runAnimations);
    this.addAnimation("walk", pathBase + "Walk (%d).png", walkAnimations);

    this.setOnEdgeBounce(true);

    this.tintColor = (int) this.pickRandom(0, 256);
    this.setPosition(
        this.pickRandom(200, Window.getInstance().getWidth() - 200),
        this.pickRandom(200, Window.getInstance().getHeight() - 200));
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
