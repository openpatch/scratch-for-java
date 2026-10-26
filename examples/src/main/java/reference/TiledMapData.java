package reference;

import org.openpatch.scratch.Shape;
import org.openpatch.scratch.tiled.TiledMap;

/**
 * Reading map data does not need a window.
 * @example.online false
 */
public class TiledMapData {
  public TiledMapData() {
    TiledMap map = new TiledMap("src/examples/java/reference/assets/tiled-data.tmx");
    System.out.println(map.getProperty("biome", "forest"));
    System.out.println(map.getWidth() + " columns, " + map.getHeight() + " rows");
    System.out.println(map.getTileWidth() + " x " + map.getTileHeight() + " pixel cells");
    int tileId = map.getTileId("Ground", 1, 0);
    Shape[] localShapes = map.getTileCollisionShapes(tileId);
    Shape[] placedShapes = map.getTileCollisionShapes("Ground", 1, 0);
    for (Shape shape : placedShapes) System.out.println(shape.getBounds());
    // Use these shapes with the game's collision rules; rendering is optional.
  }
  public static void main(String[] args) { new TiledMapData(); }
}
