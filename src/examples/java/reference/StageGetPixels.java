package reference;
import org.openpatch.scratch.*;


public class StageGetPixels {
  public StageGetPixels() {
    Stage myStage = new Stage(600, 240);

    // The raw pixels of the stage. Only the desktop version can reach them - in
    // the browser this prints a notice and hands back an empty array.
    int[] pixels = myStage.getPixels().main();
    System.out.println("the stage has " + pixels.length + " pixels");
  }

  public static void main(String[] args) {
    new StageGetPixels();
  }
}
