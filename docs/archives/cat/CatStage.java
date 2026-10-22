import org.openpatch.scratch.*;

public class CatStage extends Stage {

  // begin stage designer
  // end stage designer
  public CatStage() {
    Sprite myCat = new CatSprite();
    this.add(myCat);

    // begin stage designer
    // end stage designer
  }

  public static void main(String[] args) {
    Window myWindow = new Window(480, 360);
    myWindow.setStage(new CatStage());
  }
}
