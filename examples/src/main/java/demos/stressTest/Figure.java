package demos.stressTest;

import org.openpatch.scratch.*;

public class Figure extends Sprite {
  public FigureState state;
  int tintColor;
  boolean hasTouchedEdge = false;

  public Figure(String pathBase, int idleAnimations, int runAnimations, int walkAnimations) {
    state = FigureState.IDLE;

    this.addAnimation("idle", pathBase + "Idle (%d).png", idleAnimations);
    this.addAnimation("run", pathBase + "Run (%d).png", runAnimations);
    this.addAnimation("walk", pathBase + "Walk (%d).png", walkAnimations);

    this.tintColor = this.pickRandom(0, 200);
    this.setPosition(this.pickRandom(-200, 200), this.pickRandom(-200, 200));
    this.setDirection(this.pickRandom(0, 360));

    // begin sprite editor
    // end sprite editor
  }

  public void run() {
    this.ifOnEdgeBounce();
    this.setColorEffect(this.tintColor);

    if (isTouchingMousePointer()) {
      state = FigureState.WALK;
    } else if (state == FigureState.WALK) {
      state = FigureState.IDLE;
    }

    if (isTouchingEdge() && !hasTouchedEdge) {
      hasTouchedEdge = true;
    } else if (!isTouchingEdge() && hasTouchedEdge) {
      hasTouchedEdge = false;
    }

    switch (state) {
      case IDLE:
        this.setAnimationInterval(0.1);
        this.playAnimation("idle");
        break;
      case RUN:
        this.setAnimationInterval(0.05);
        this.playAnimation("run");
        move(4);
        break;
      case WALK:
        this.setAnimationInterval(0.1);
        this.playAnimation("walk");
        move(2);
        break;
    }
  }
}
