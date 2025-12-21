import org.openpatch.scratch.*;

public class SpriteSetOrigin extends Window {
  public SpriteSetOrigin() {
    super(800, 600);
    var stage = new Stage();
    
    // Create a sprite with centered origin (default)
    var centerSprite = new Sprite("slime", "assets/slime.png");
    centerSprite.setPosition(200, 0);
    centerSprite.setOrigin(Origin.CENTER);
    stage.add(centerSprite);
    
    // Create a sprite with top-left origin
    var topLeftSprite = new Sprite("slime", "assets/slime.png");
    topLeftSprite.setPosition(400, 0);
    topLeftSprite.setOrigin(Origin.TOP_LEFT);
    stage.add(topLeftSprite);
    
    // Create a sprite with bottom-right origin
    var bottomRightSprite = new Sprite("slime", "assets/slime.png");
    bottomRightSprite.setPosition(600, 0);
    bottomRightSprite.setOrigin(Origin.BOTTOM_RIGHT);
    stage.add(bottomRightSprite);
    
    // Create a sprite with custom origin
    var customSprite = new Sprite("slime", "assets/slime.png");
    customSprite.setPosition(200, -150);
    customSprite.setOrigin(-30, -30);
    stage.add(customSprite);
    
    // Create rotating sprites to test rotation with different origins
    var rotatingCenter = new Sprite("slime", "assets/slime.png") {
      @Override
      public void run() {
        this.turnRight(1);
      }
    };
    rotatingCenter.setPosition(400, -150);
    rotatingCenter.setOrigin(Origin.CENTER);
    stage.add(rotatingCenter);
    
    var rotatingTopLeft = new Sprite("slime", "assets/slime.png") {
      @Override
      public void run() {
        this.turnRight(1);
      }
    };
    rotatingTopLeft.setPosition(600, -150);
    rotatingTopLeft.setOrigin(Origin.TOP_LEFT);
    stage.add(rotatingTopLeft);
    
    this.setStage(stage);
    this.setDebug(true);
  }

  public static void main(String[] args) {
    new SpriteSetOrigin();
  }
}
