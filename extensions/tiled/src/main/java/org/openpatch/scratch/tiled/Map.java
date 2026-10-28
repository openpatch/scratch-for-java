package org.openpatch.scratch.tiled;


class Map {
  public java.util.List<Property> properties;

  public int compressionlevel;
  public int height;
  public int infinite;

  public MapLayer[] layers;

  public ObjectGroup[] objectGroups;

  public String orientation;
  public String renderorder;
  public String tiledversion;
  public int tileheight;

  public Tileset[] tilesets;

  public int tilewidth;
  public String type;
  public int width;

  /** Group layers (Tiled 1.2+); their layers count as the map's own. */
  public LayerGroup[] groups;
}
