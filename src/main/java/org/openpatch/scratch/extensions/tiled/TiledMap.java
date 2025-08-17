package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.fs.File;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Stamp;

/**
 * The TiledMap class represents a map created using the Tiled map editor. It
 * provides methods to
 * load the map from an XML file, retrieve objects from layers, and stamp layers
 * onto the foreground
 * or background of a stage.
 *
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * TiledMap map = new TiledMap("assets/map.tmx", stage);
 * map.stampLayerToForeground("foreground");
 *
 * for (MapObject object : map.getObjectsFromLayer("objects")) {
 *   if (object.type.equals("player")) {
 *     Player p = new Player(object.x, object.y);
 *     stage.addSprite(p);
 *   }
 * }
 *
 * }</pre>
 */
public class TiledMap {

  private Map map;
  private Stage stage;
  private ConcurrentHashMap<Integer, Image> tiles;

  /**
   * Constructs a new TiledMap object.
   *
   * @param path  the file path to the Tiled map XML file
   * @param stage the stage to which this Tiled map belongs
   */
  public TiledMap(String path, Stage stage) {
    this.stage = stage;
    this.map = File.loadXML(path, Map.class);
    this.tiles = new ConcurrentHashMap<>();
    for (var tileset : this.map.tilesets) {
      var dir = Paths.get(path).getParent();
      var spriteSheetPath = Paths.get(dir.toString(), tileset.image.source).toString();
      var firstId = tileset.firstgid;
      for (int index = 0; index < tileset.tilecount; index++) {
        var x = index % tileset.columns;
        var y = index / tileset.columns;

        var image = new Image(
            "",
            spriteSheetPath,
            x * tileset.tilewidth,
            y * tileset.tileheight,
            tileset.tilewidth,
            tileset.tileheight);
        image.setSize(tileset.tilewidth + 1, tileset.tileheight + 1);
        tiles.put(firstId + index, image);
      }
    }
  }

  private MapLayer getLayer(String name) {
    for (var layer : this.map.layers) {
      if (layer.name.equals(name)) {
        return layer;
      }
    }
    return null;
  }

  private ObjectGroup getObjectGroup(String name) {
    for (var og : this.map.objectGroups) {
      if (og.name.equals(name)) {
        return og;
      }
    }
    return null;
  }

  /**
   * Retrieves the objects from a specified layer in the tiled map.
   *
   * @param name the name of the layer from which to retrieve objects
   * @return an array of MapObject from the specified layer, or null if the layer
   *         does not exist
   */
  public MapObject[] getObjectsFromLayer(String name) {
    var og = getObjectGroup(name);
    if (og != null) {
      for (var object : og.objects) {
        object.y *= -1;
      }
      return og.objects;
    }

    return null;
  }

  private Queue<Stamp> stampLayer(String name) {
    var layer = this.getLayer(name);
    var stamps = new ConcurrentLinkedQueue<Stamp>();

    for (int index = 0; index < layer.data.length; index++) {
      var tx = index % layer.width;
      var ty = index / layer.width;

      var x = tx * map.tilewidth + map.tilewidth / 2;
      var y = -ty * map.tileheight - map.tileheight / 2;

      var tile = tiles.get(layer.data[index]);
      if (tile == null) {
        continue;
      }

      stamps.add(new Stamp(tile, x - 0.5, y - 0.5));
    }

    return stamps;
  }

  /**
   * Stamps the specified layer to the foreground.
   *
   * @param name the name of the layer to be stamped to the foreground
   */
  public void stampLayerToForeground(String name) {
    var stamps = stampLayer(name);
    stage.addStampsToForeground(stamps);
  }

  /**
   * Stamps a specified layer onto the background.
   *
   * @param name the name of the layer to be stamped onto the background
   */
  public void stampLayerToBackground(String name) {
    var stamps = stampLayer(name);
    stage.addStampsToBackground(stamps);
  }

  public String toString() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.map);
      return json;
    } catch (JsonProcessingException e) {
      System.out.println(e);
      return "";
    }
  }
}
