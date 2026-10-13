package reference;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.tiled.TiledMap;
import org.openpatch.scratch.Window;

public class TiledMapRender {
  public TiledMapRender() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    var map = new TiledMap("assets/map.json", myStage);
    System.out.println(map);
  }

  public static void main(String[] args) {
    new TiledMapRender();
  }
}
