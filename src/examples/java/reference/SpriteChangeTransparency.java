package reference;
import org.openpatch.scratch.*;

public class SpriteChangeTransparency {
  public SpriteChangeTransparency() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("zeta", "slimeGreen");
    myStage.add(mySprite);

    // A sprite starts at 0, fully solid. Stepping up towards 100 fades it out.
    while (true) {
      for (int i = 0; i < 10; i++) {
        mySprite.changeTransparency(10);
        myStage.wait(100);
      }
      mySprite.setTransparency(0);
      myStage.wait(500);
    }
  }

  public static void main(String[] args) {
    new SpriteChangeTransparency();
  }
}
