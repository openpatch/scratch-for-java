enum CharacterState {
  IDLE, RUN, WALK
}

class Character extends ScratchSprite {

  int runAnimationFrame = 0;
  String[] runAnimation;

  int idleAnimationFrame = 0;
  String[] idleAnimation;

  int walkAnimationFrame = 0;
  String[] walkAnimation;

  CharacterState state;

  int tintColor;
  
  boolean hasTouchedEdge = false;

  Character(String pathBase, int idleAnimations, int runAnimations, int walkAnimations) {
    //String pathBase = "assets/" + name + "/";
    super();
    state = CharacterState.IDLE;
    
    this.addSound("bump", "assets/bump.wav");
    this.addSound("run", "assets/run.wav");

    this.addTimer("idle");
    idleAnimation = new String[idleAnimations];
    for (int i = 1; i <= idleAnimations; i++) {
      this.addCostume("idle_" + i, pathBase + "Idle (" + i + ").png");
      idleAnimation[i-1] = "idle_" + i;
    }

    this.addTimer("run");
    runAnimation = new String[runAnimations];
    for (int i = 1; i <= runAnimations; i++) {
      this.addCostume("run_" + i, pathBase + "Run (" + i + ").png");
      runAnimation[i-1] = "run_" + i;
    }

    this.addTimer("walk");
    walkAnimation = new String[walkAnimations];
    for (int i = 1; i <= walkAnimations; i++) {
      this.addCostume("walk_" + i, pathBase + "Walk (" + i + ").png");
      walkAnimation[i-1] = "walk_" + i;
    }

    this.setOnEdgeBounce(true); 
    this.setSize(30);

    this.tintColor = (int) random(0, 256);
    this.setPosition(random(200, width - 200), random(200, height - 200));
    this.setRotation(random(0, 360));
  }

  void draw() {
    this.setTint(this.tintColor);
    super.draw();
    
    if(isTouchingMousePointer()) {
      state = CharacterState.WALK;
    } else if(state == CharacterState.WALK) {
      state = CharacterState.IDLE;
    }
    
    if(isTouchingEdge() && !hasTouchedEdge) {
      this.playSound("bump");
      hasTouchedEdge = true;
  } else if(!isTouchingEdge() && hasTouchedEdge) {
      hasTouchedEdge = false;
  }

    switch (state) {
    case IDLE:
      if (this.getTimer("idle").everyMillis(100)) {
        idleAnimationFrame = (idleAnimationFrame + 1) % idleAnimation.length;
        this.switchCostume(idleAnimation[idleAnimationFrame]);
      }
      break;
    case RUN:
      if (this.getTimer("run").everyMillis(50)) {
        runAnimationFrame = (runAnimationFrame + 1) % runAnimation.length;
        this.switchCostume(runAnimation[runAnimationFrame]);
      }
      move(4);
      break;
    case WALK:
      if (this.getTimer("walk").everyMillis(100)) {
        walkAnimationFrame = (walkAnimationFrame + 1) % walkAnimation.length;
        this.switchCostume(walkAnimation[walkAnimationFrame]);
      }
      move(4);
      break;
    }
  }
}
