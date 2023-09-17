import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.tiled.TiledMap;

public class TiledMapRender extends Stage {
  public TiledMapRender() {
    super(800, 600);
    var map = new TiledMap("assets/map.json", this);
    System.out.println(map);
  }

  public static void main(String[] args) {
    new TiledMapRender();
  }
}
