package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.fs.File;
import org.openpatch.scratch.internal.Image;
import org.openpatch.scratch.internal.Stamp;

public class TiledMap {

  private Map map;
  private Stage stage;
  private ConcurrentHashMap<Integer, Image> tiles;

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

        var image =
            new Image(
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

  private void stampLayer(String name, Queue<Stamp> stamps) {
    var layer = this.getLayer(name);

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
  }

  public void stampLayerToForeground(String name) {
    stampLayer(name, stage.foregroundStamps);
  }

  public void stampLayerToBackground(String name) {
    stampLayer(name, stage.backgroundStamps);
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
