import org.openpatch.scratch.*;

public class SpriteGetOrigin extends Window {
  public SpriteGetOrigin() {
    super(800, 600);
    var stage = new Stage();
    
    var sprite = new Sprite("slime", "assets/slime.png");
    sprite.setPosition(0, 0);
    
    // Test getting default origin
    System.out.println("Default origin: " + sprite.getOrigin());
    System.out.println("Default originX: " + sprite.getOriginX());
    System.out.println("Default originY: " + sprite.getOriginY());
    
    // Test setting and getting predefined origin
    sprite.setOrigin(Origin.TOP_LEFT);
    System.out.println("After setOrigin(TOP_LEFT): " + sprite.getOrigin());
    
    // Test setting and getting custom origin
    sprite.setOrigin(50, -25);
    System.out.println("After setOrigin(50, -25):");
    System.out.println("  Origin mode: " + sprite.getOrigin());
    System.out.println("  originX: " + sprite.getOriginX());
    System.out.println("  originY: " + sprite.getOriginY());
    
    stage.add(sprite);
    this.setStage(stage);
  }

  public static void main(String[] args) {
    new SpriteGetOrigin();
  }
}
