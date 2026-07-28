package reference;
import org.openpatch.scratch.*;


public class WindowGetTextureSampling {
  public WindowGetTextureSampling() {
    Window.useTextureSampling(TextureSampling.POINT);
    System.out.println("costumes are scaled with " + Window.getTextureSampling());
  }

  public static void main(String[] args) {
    new WindowGetTextureSampling();
  }
}
