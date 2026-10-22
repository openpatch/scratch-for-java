package org.openpatch.scratch.tiled;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Shape;
import org.openpatch.scratch.extension.Canvas;

/**
 * A map made with the Tiled editor (TMX or TMJ): its tile layers are shown in the stage's layers
 * and drawn every frame, its object layers can spawn sprites, its tiles carry properties and
 * collision shapes.
 *
 * <p>
 * Example usage:
 *
 * <pre>{@code
 * TiledMap map = new TiledMap("assets/map.tmx", stage);
 * map.showLayer("ground", Layer.BACKGROUND);
 * map.showLayer("treetops", Layer.FOREGROUND);
 *
 * for (MapObject object : map.getObjectsFromLayer("objects")) {
 *   if (object.type.equals("player")) {
 *     Player p = new Player(object.x, object.y);
 *     stage.add(p);
 *   }
 * }
 *
 * }</pre>
 */
public class TiledMap {

  private Map map;
  private Stage stage;
  /** A tile: its region of the tileset image. */
  record TileRegion(String sheet, int x, int y, int width, int height) {}

  private ConcurrentHashMap<Integer, TileRegion> tiles;
  private final java.util.Map<Integer, Tile> tileDefinitions = new java.util.HashMap<>();

  /**
   * Loads map data without attaching it to a stage. Properties, tile IDs and
   * collision shapes can be read without a window. To stamp or spawn, use the
   * constructor that takes a stage.
   * @param path a TMX file
   *
   *
   * @example.files TiledMapData.java
   */
  public TiledMap(String path) {
    this(path, null);
  }

  /**
   * Constructs a new TiledMap object.
   *
   * @param path  the file path to the Tiled map XML file
   * @param stage the stage to which this Tiled map belongs
   *
   * @example.files TiledMapData.java
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
        var region = new TileRegion(spriteSheetPath,
            tileset.margin + x * (tileset.tilewidth + tileset.spacing),
            tileset.margin + y * (tileset.tileheight + tileset.spacing),
            tileset.tilewidth,
            tileset.tileheight);
        tiles.put(firstId + index, region);
      }
      for (Tile tile : tileset.tile == null ? new Tile[0] : tileset.tile) {
        this.tileDefinitions.put(firstId + tile.id, tile);
      }
    }
  }

  /**
   * @return the number of map columns
   *
   * @example.files TiledMapData.java
   */
  public int getWidth() { return this.map.width; }

  /**
   * @return the number of map rows
   *
   * @example.files TiledMapData.java
   */
  public int getHeight() { return this.map.height; }

  /**
   * @return the width of a map cell in pixels
   *
   * @example.files TiledMapData.java
   */
  public int getTileWidth() { return this.map.tilewidth; }

  /**
   * @return the height of a map cell in pixels
   *
   * @example.files TiledMapData.java
   */
  public int getTileHeight() { return this.map.tileheight; }

  /**
   * Reads a custom property of the map itself, such as its biome or tutorial.
   * @param name property name
   *
   * @return its string value, including multiline strings
   * @throws java.util.NoSuchElementException if the property does not exist
   *
   *
   * @example.files TiledMapData.java
   */
  public String getProperty(String name) {
    for (Property property : this.map.properties == null ? java.util.List.<Property>of() : this.map.properties) {
      if (name.equals(property.name)) return property.value;
    }
    throw new java.util.NoSuchElementException("Map property '" + name + "' does not exist.");
  }

  /**
   * Reads a map property, with a default for maps that omit it.
   * @param name property name
   * @param defaultValue value when absent
   *
   * @return the stored value or the default
   *
   *
   * @example.files TiledMapData.java
   */
  public String getProperty(String name, String defaultValue) {
    try {
      return this.getProperty(name);
    } catch (java.util.NoSuchElementException absent) {
      return defaultValue;
    }
  }

  /** @param name property name
   * @return its integer value
   *
   * @example.files TiledMapData.java
   */
  public int getPropertyInt(String name) { return Integer.parseInt(this.getProperty(name)); }

  /** @param name property name
   * @return its floating-point value
   *
   * @example.files TiledMapData.java
   */
  public float getPropertyFloat(String name) { return Float.parseFloat(this.getProperty(name)); }

  /** @param name property name
   * @return its boolean value
   *
   * @example.files TiledMapData.java
   */
  public boolean getPropertyBoolean(String name) { return Boolean.parseBoolean(this.getProperty(name)); }

  /**
   * Reads a global tile ID, without flip bits. Columns and rows are local to
   * the named layer, starting at its top-left cell. Zero means an empty cell,
   * including cells outside the layer. An unknown layer is an error.
   * @param layerName tile layer name, also within a group
   * @param column column index
   * @param row row index
   *
   * @return global ID, or zero for an empty/outside cell
   *
   *
   * @example.files TiledMapData.java
   */
  public int getTileId(String layerName, int column, int row) {
    MapLayer layer = this.requireLayer(layerName);
    if (column < 0 || row < 0 || column >= layer.width || row >= layer.height) return 0;
    return layer.data[row * layer.width + column];
  }

  /**
   * Returns collision outlines defined in a tile's tileset object group.
   * Coordinates start at the tile's top-left; x points right and y points up.
   * Shapes are independent copies and do not modify the map when transformed.
   * @param tileId global tile ID (zero and unknown IDs have no shapes)
   *
   * @return rectangles, ellipses or polygons for this tile
   *
   *
   * @example.files TiledMapData.java
   */
  public Shape[] getTileCollisionShapes(int tileId) {
    Tile tile = this.tileDefinitions.get(tileId);
    if (tile == null || tile.objectgroup == null || tile.objectgroup.objects == null) return new Shape[0];
    java.util.List<Shape> shapes = new java.util.ArrayList<>();
    for (MapObject object : tile.objectgroup.objects) {
      shapes.add(object.getShape().rotate(object.rotation, 0, 0)
          .translate(object.x, object.y).scale(1, -1));
    }
    return shapes.toArray(Shape[]::new);
  }

  /**
   * Returns a cell's collision outlines in map/stage coordinates: the map's
   * top-left is (0,0), x points right and y points up, matching map stamping.
   * Tiled's diagonal, horizontal and vertical flips are applied in that order.
   * @param layerName tile layer name
   * @param column column index, starting at zero
   * @param row row index, starting at zero
   *
   * @return positioned outlines, or an empty array outside/without collision
   *
   *
   * @example.files TiledMapData.java
   */
  public Shape[] getTileCollisionShapes(String layerName, int column, int row) {
    int id = this.getTileId(layerName, column, row);
    Shape[] shapes = this.getTileCollisionShapes(id);
    if (shapes.length == 0) return shapes;
    MapLayer layer = this.requireLayer(layerName);
    int index = row * layer.width + column;
    int flips = layer.flips == null ? 0 : layer.flips[index];
    TileRegion tile = this.tiles.get(id);
    double width = tile.width();
    double height = tile.height();
    for (int i = 0; i < shapes.length; i++) {
      Shape shape = shapes[i].scale(1, -1).translate(-width / 2, -height / 2);
      if ((flips & 1) != 0) shape = shape.scale(1, -1).rotate(90, 0, 0);
      shapes[i] = shape.scale((flips & 4) != 0 ? -1 : 1, (flips & 2) != 0 ? -1 : 1)
          .translate(column * this.map.tilewidth + width / 2,
              row * this.map.tileheight + height / 2).scale(1, -1);
    }
    return shapes;
  }

  private MapLayer requireLayer(String name) {
    MapLayer layer = this.getLayer(name);
    if (layer == null) throw new IllegalArgumentException(unknownLayer(this.map, name));
    return layer;
  }

  private void requireStage() {
    if (this.stage == null) throw new IllegalStateException(
        "This map has no stage. Use new TiledMap(path, stage) to show layers or spawn.");
  }

  /**
   * Reads a .tmx or .tsx file. Unlike File.loadXML this does not swallow the
   * reason: "this map is infinite" reaches the student as the error message.
   */
  static <T> T read(String path, Class<T> type) {
    var file = new java.io.File(resolve(path));
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

  /** Clear all four Tiled flags, including a retained hexagonal rotation flag. */
  static final long GID_MASK = 0x0FFFFFFFL;

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
   * Creates a sprite for each object of a layer whose type names a sprite class,
   * and adds it to the stage. In Tiled, give an object the type (called
   * "class" in newer versions of Tiled) {@code Player}, and the map makes a
   * {@code new Player()} where the object is.
   *
   * <p>
   * The sprite's class needs one of these constructors, tried in this order:
   * one that takes the {@link MapObject} (to read its name and properties), one
   * that takes {@code (double x, double y)}, or one without parameters. Either
   * way the sprite is put where the object is. An object drawn as an area
   * (a rectangle, an ellipse or a polygon) also gives the sprite that area as
   * its hitbox, which is how walls and water work without a costume.
   *
   * <p>
   * Objects whose type is not a sprite class, or that have no type, are left
   * alone, so a layer can hold markers the program reads itself.
   *
   * <pre>{@code
   * TiledMap map = new TiledMap("level.tmx", this);
   * map.spawn("Objects");
   * }</pre>
   *
   * @param layerName the object layer
   * @return the sprites that were made, in the order of the layer
   * @example.files TiledSpawn.java
   * @throws IllegalArgumentException if the map has no such object layer
   */
  public java.util.List<Sprite> spawn(String layerName) {
    this.requireStage();
    var objects = this.getObjectsFromLayer(layerName);
    if (objects == null) {
      throw new IllegalArgumentException("The map has no object layer " + layerName);
    }
    var made = new java.util.ArrayList<Sprite>();
    for (var object : objects) {
      Class<? extends Sprite> type = spriteClass(object.type);
      if (type == null) {
        continue;
      }
      Sprite sprite = make(type, object);
      sprite.setPosition(object.x, object.y);
      if (object.point == null && object.width > 0 && object.height > 0
          || object.polygon != null) {
        sprite.setHitbox(object.getShape());
      }
      this.stage.add(sprite);
      made.add(sprite);
    }
    return made;
  }

  /** The sprite class a type names, looked for beside the stage's class first. */
  private Class<? extends Sprite> spriteClass(String type) {
    if (type == null || type.isBlank()) {
      return null;
    }
    var loader = this.stage.getClass().getClassLoader();
    String pkg = this.stage.getClass().getPackageName();
    for (String name : pkg.isEmpty() ? new String[] {type}
        : new String[] {pkg + "." + type, type}) {
      try {
        Class<?> found = Class.forName(name, false, loader);
        if (Sprite.class.isAssignableFrom(found)) {
          return found.asSubclass(Sprite.class);
        }
        return null;
      } catch (ClassNotFoundException | LinkageError e) {
        // try the next name
      }
    }
    return null;
  }

  private static Sprite make(Class<? extends Sprite> type, MapObject object) {
    try {
      for (Object[] attempt : new Object[][] {
          {new Class<?>[] {MapObject.class}, new Object[] {object}},
          {new Class<?>[] {double.class, double.class}, new Object[] {object.x, object.y}},
          {new Class<?>[0], new Object[0]}}) {
        try {
          var constructor = type.getDeclaredConstructor((Class<?>[]) attempt[0]);
          constructor.setAccessible(true);
          return constructor.newInstance((Object[]) attempt[1]);
        } catch (NoSuchMethodException e) {
          // try the next constructor
        }
      }
    } catch (java.lang.reflect.InvocationTargetException e) {
      var cause = e.getCause();
      if (cause instanceof RuntimeException runtime) throw runtime;
      if (cause instanceof Error error) throw error;
      throw new IllegalStateException(cause);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Cannot make a " + type.getSimpleName() + ": " + e, e);
    }
    throw new IllegalArgumentException("The map object " + object.id + " is a "
        + type.getSimpleName() + ", but " + type.getSimpleName() + " has no constructor"
        + " spawn can use: (MapObject object), (double x, double y) or ().");
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

  /**
   * Shows a tile layer of the map in a layer of the stage, drawn every frame from now on (only
   * the tiles the camera shows). The map's top left corner is at (0, 0) of the world; the map
   * extends right and down.
   *
   * <pre>{@code
   * map.showLayer("ground", Layer.BACKGROUND);
   * map.showLayer("trees", Layer.FOREGROUND);
   * }</pre>
   *
   * @param name the tile layer's name in Tiled
   * @param layer the stage's layer to draw it in
   */
  public void showLayer(String name, Layer layer) {
    this.requireStage();
    MapLayer mapLayer = this.getLayer(name);
    if (mapLayer == null) {
      throw new IllegalArgumentException(unknownLayer(map, name));
    }
    TiledExtension.shown(this.stage).add(new TiledExtension.Shown(this, mapLayer, layer));
  }

  /**
   * Stops drawing a tile layer shown with {@link #showLayer(String, Layer)}.
   *
   * @param name the tile layer's name in Tiled
   */
  public void hideLayer(String name) {
    this.requireStage();
    TiledExtension.shown(this.stage).removeIf(s -> s.map() == this && s.mapLayer().name.equals(name));
  }

  /**
   * Draws a map layer's tiles that lie in the visible rectangle (world coordinates).
   */
  void draw(MapLayer layer, Canvas canvas, double left, double bottom, double right, double top) {
    int tw = map.tilewidth, th = map.tileheight;
    int firstColumn = Math.max(0, (int) Math.floor(left / tw)), lastColumn = Math.min(layer.width - 1,
        (int) Math.ceil(right / tw));
    int firstRow = Math.max(0, (int) Math.floor(-top / th)), lastRow = Math.min(layer.height - 1,
        (int) Math.ceil(-bottom / th));
    for (int ty = firstRow; ty <= lastRow; ty++) {
      for (int tx = firstColumn; tx <= lastColumn; tx++) {
        int index = ty * layer.width + tx;
        if (index >= layer.data.length) {
          continue;
        }
        TileRegion tile = tiles.get(layer.data[index]);
        if (tile == null) {
          continue;
        }
        int flips = layer.flips == null ? 0 : layer.flips[index];
        double l = tx * tw, t = -ty * th, r = l + tile.width(), b = t - tile.height();
        // Tiled's flips: 4 horizontal, 2 vertical, 1 diagonal (applied first)
        double[][] corners = {{l, t}, {r, t}, {r, b}, {l, b}};
        if ((flips & 1) != 0) {
          corners = new double[][] {corners[0], corners[3], corners[2], corners[1]};
        }
        if ((flips & 4) != 0) {
          corners = new double[][] {corners[1], corners[0], corners[3], corners[2]};
        }
        if ((flips & 2) != 0) {
          corners = new double[][] {corners[3], corners[2], corners[1], corners[0]};
        }
        canvas.drawImageQuad(tile.sheet(), tile.x(), tile.y(), tile.width(), tile.height(), new double[] {
            corners[0][0], corners[0][1], corners[1][0], corners[1][1], corners[2][0], corners[2][1],
            corners[3][0], corners[3][1]});
      }
    }
  }

  /**
   * Where a path points: {@code ~} is the home folder, an absolute path stays, a plain file on the
   * classpath is used there, anything else is relative to the working directory.
   */
  static String resolve(String path) {
    if (path.startsWith("~")) {
      path = System.getProperty("user.home") + path.substring(1);
    }
    java.nio.file.Path fsPath = java.nio.file.Path.of(path);
    if (fsPath.isAbsolute() || java.nio.file.Files.exists(fsPath)) {
      return fsPath.toString();
    }
    var resource = Thread.currentThread().getContextClassLoader().getResource(path);
    if (resource != null && "file".equals(resource.getProtocol())) {
      try {
        return java.nio.file.Path.of(resource.toURI()).toString();
      } catch (java.net.URISyntaxException e) {
        throw new IllegalArgumentException("Invalid resource URI: " + resource, e);
      }
    }
    return fsPath.toString();
  }

  @Override
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
