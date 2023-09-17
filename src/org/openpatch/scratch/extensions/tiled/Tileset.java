package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

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
  @JacksonXmlElementWrapper(useWrapping = false)
  public Tile[] tile;
}
