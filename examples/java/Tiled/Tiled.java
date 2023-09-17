import org.openpatch.scratch.Window;

public class Tiled extends Window {
  public Tiled() {
    super(800, 600, "assets");

    this.setDebug(true);
    this.addStage("level", new TiledLevel());
  }

  public static void main(String[] args) {
    new Tiled();
  }
}
