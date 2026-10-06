package org.openpatch.scratch.extensions.tiled;

import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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
  @JacksonXmlProperty(isAttribute = true)
  public String source;

  @JacksonXmlElementWrapper(useWrapping = false)
  public Tile[] tile;
}
