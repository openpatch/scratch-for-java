package org.openpatch.scratch.extensions.tiled;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.StageAccess;
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
    this.map = read(path, Map.class);
    if (this.map.orientation != null && !this.map.orientation.equals("orthogonal")) {
      throw new IllegalArgumentException("The map " + path + " is " + this.map.orientation
          + ": Scratch for Java draws orthogonal maps only.");
    }
    this.tiles = new ConcurrentHashMap<>();
    // a map file without a folder ("level.tmx") lives in the working directory
    var mapDir = Paths.get(path).getParent();
    String dir = mapDir == null ? "" : mapDir.toString();
    for (var entry : this.map.tilesets == null ? new Tileset[0] : this.map.tilesets) {
      var tileset = entry;
      String tilesetDir = dir;
      if (entry.source != null && !entry.source.isEmpty()) {
        // an external tileset (.tsx): its image path is relative to the .tsx
        var tsxPath = Paths.get(dir, entry.source);
        var external = read(tsxPath.toString(), Tileset.class);
        external.firstgid = entry.firstgid;
        tileset = external;
        tilesetDir = tsxPath.getParent() == null ? "" : tsxPath.getParent().toString();
      }
      if (tileset.image == null) {
        throw new IllegalArgumentException("The tileset " + tileset.name + " in " + path
            + " is a collection of images; Scratch for Java needs a tileset made from one "
            + "image.");
      }
      var spriteSheetPath = Paths.get(tilesetDir, tileset.image.source).toString();
      var firstId = tileset.firstgid;
      int columns = Math.max(1, tileset.columns);
      for (int index = 0; index < tileset.tilecount; index++) {
        var x = index % columns;
        var y = index / columns;

        // margin around the image and spacing between tiles, as Tiled cuts them
        var image = new Image(
            "",
            spriteSheetPath,
            tileset.margin + x * (tileset.tilewidth + tileset.spacing),
            tileset.margin + y * (tileset.tileheight + tileset.spacing),
            tileset.tilewidth,
            tileset.tileheight);
        image.setSize(tileset.tilewidth + 1, tileset.tileheight + 1);
        tiles.put(firstId + index, image);
      }
    }
  }

  /**
   * Reads a .tmx or .tsx file. Unlike File.loadXML this does not swallow the
   * reason: "this map is infinite" reaches the student as the error message.
   */
  static <T> T read(String path, Class<T> type) {
    var file = new java.io.File(org.openpatch.scratch.internal.Applet.getPath(path));
    if (!file.isFile()) {
      throw new IllegalArgumentException("The Tiled file " + path + " does not exist.");
    }
    var mapper = tools.jackson.dataformat.xml.XmlMapper.builder()
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
    try {
      return mapper.readValue(file, type);
    } catch (RuntimeException e) {
      Throwable cause = e;
      while (cause.getCause() != null && cause.getCause() != cause) {
        cause = cause.getCause();
      }
      throw new IllegalArgumentException("Cannot read the Tiled file " + path + ": "
          + cause.getMessage(), e);
    }
  }

  /** Tiled keeps flip flags in the top three bits of a tile id. */
  static final long GID_MASK = 0x1FFFFFFFL;

  /** Every tile layer, including those inside group layers. */
  private java.util.List<MapLayer> allLayers() {
    return allLayers(map);
  }

  static java.util.List<MapLayer> allLayers(Map map) {
    var out = new java.util.ArrayList<MapLayer>();
    if (map.layers != null) {
      out.addAll(java.util.List.of(map.layers));
    }
    collect(map.groups, out, null);
    return out;
  }

  /** Every object layer, including those inside group layers. */
  private java.util.List<ObjectGroup> allObjectGroups() {
    return allObjectGroups(map);
  }

  static java.util.List<ObjectGroup> allObjectGroups(Map map) {
    var out = new java.util.ArrayList<ObjectGroup>();
    if (map.objectGroups != null) {
      out.addAll(java.util.List.of(map.objectGroups));
    }
    collect(map.groups, null, out);
    return out;
  }

  private static void collect(LayerGroup[] groups, java.util.List<MapLayer> layers,
      java.util.List<ObjectGroup> objects) {
    if (groups == null) {
      return;
    }
    for (var group : groups) {
      if (layers != null && group.layers != null) {
        layers.addAll(java.util.List.of(group.layers));
      }
      if (objects != null && group.objectGroups != null) {
        objects.addAll(java.util.List.of(group.objectGroups));
      }
      collect(group.groups, layers, objects);
    }
  }

  private MapLayer getLayer(String name) {
    for (var layer : allLayers()) {
      if (layer.name.equals(name)) {
        return layer;
      }
    }
    return null;
  }

  /** "There is no tile layer X; the map has: A, B" (an object layer is named as such). */
  static String unknownLayer(Map map, String name) {
    var names = new java.util.ArrayList<String>();
    for (var layer : allLayers(map)) {
      names.add(layer.name);
    }
    for (var og : allObjectGroups(map)) {
      if (og.name.equals(name)) {
        return "\"" + name + "\" is an object layer: read it with getObjectsFromLayer. "
            + "Tile layers: " + String.join(", ", names);
      }
    }
    return "The map has no tile layer \"" + name + "\". Tile layers: "
        + String.join(", ", names);
  }

  private ObjectGroup getObjectGroup(String name) {
    for (var og : allObjectGroups()) {
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
    return objectsFromLayer(map, name);
  }

  /**
   * The objects of a layer with y pointing up like on the stage. The y values
   * are turned once: asking twice used to turn them back.
   */
  static MapObject[] objectsFromLayer(Map map, String name) {
    for (var og : allObjectGroups(map)) {
      if (og.name.equals(name)) {
        if (!og.stageCoordinates) {
          for (var object : og.objects == null ? new MapObject[0] : og.objects) {
            object.y *= -1;
          }
          og.stageCoordinates = true;
        }
        return og.objects == null ? new MapObject[0] : og.objects;
      }
    }
    return null;
  }

  private Queue<Stamp> stampLayer(String name) {
    var layer = this.getLayer(name);
    if (layer == null) {
      throw new IllegalArgumentException(unknownLayer(map, name));
    }
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

      int flips = layer.flips == null ? 0 : layer.flips[index];
      stamps.add(new Stamp(tile, x - 0.5, y - 0.5, (flips & 4) != 0, (flips & 2) != 0,
          (flips & 1) != 0));
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
    StageAccess.get().stamp(stage, stamps, Layer.FOREGROUND);
  }

  /**
   * Stamps a specified layer onto the background.
   *
   * @param name the name of the layer to be stamped onto the background
   */
  public void stampLayerToBackground(String name) {
    var stamps = stampLayer(name);
    StageAccess.get().stamp(stage, stamps, Layer.BACKGROUND);
  }

  public String toString() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.map);
      return json;
    } catch (JacksonException e) {
      System.out.println(e);
      return "";
    }
  }
}
