package reference;
import org.openpatch.scratch.*;


public class SpriteGetShaders {
  public SpriteGetShaders() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Shaders are drawn by the graphics card and only exist in the desktop
    // version - in the browser this prints a notice and does nothing.
    System.out.println(mySprite.getShaders());
  }

  public static void main(String[] args) {
    new SpriteGetShaders();
  }
}
