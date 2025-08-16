package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.tiled.TiledMap;

public class TiledMapRender {
  public TiledMapRender() {
    var myStage = new Stage(600, 240);
    var map = new TiledMap("assets/map.json", myStage);
    System.out.println(map);
    myStage.exit();
  }

  public static void main(String[] args) {
    new TiledMapRender();
  }
}
