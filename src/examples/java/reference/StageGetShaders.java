package reference;
import org.openpatch.scratch.*;


public class StageGetShaders {
  public StageGetShaders() {
    Stage myStage = new Stage(600, 240);

    // Shaders are drawn by the graphics card and only exist in the desktop
    // version - in the browser this prints a notice and does nothing.
    System.out.println(myStage.getShaders());
  }

  public static void main(String[] args) {
    new StageGetShaders();
  }
}
