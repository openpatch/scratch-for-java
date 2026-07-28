package reference;
import org.openpatch.scratch.*;


public class WindowUseTextureSampling {
  public WindowUseTextureSampling() {
    // POINT keeps the pixels of a costume square when it is scaled up, which
    // is what pixel art wants. It has to be said before the window is built.
    Window.useTextureSampling(TextureSampling.POINT);
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    mySprite.setSize(400);
    myStage.add(mySprite);
    myWindow.setStage(myStage);
  }

  public static void main(String[] args) {
    new WindowUseTextureSampling();
  }
}
