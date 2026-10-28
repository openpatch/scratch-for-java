package org.openpatch.scratch.tiled;


class Tileset {
  public int columns;
  public int firstgid;
  public TilesetImage image;
  public int margin;
  public String name;
  public int spacing;
  public int tilecount;
  public int tileheight;
  public int tilewidth;

  /** An external tileset (.tsx), relative to the map; loaded by TiledMap. */
  public String source;

  public Tile[] tile;
}
