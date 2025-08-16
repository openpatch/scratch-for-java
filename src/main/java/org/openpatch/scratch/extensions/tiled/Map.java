package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

class Map {
  public int compressionlevel;
  public int height;
  public int infinite;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("layer")
  public MapLayer[] layers;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("objectgroup")
  public ObjectGroup[] objectGroups;

  public String orientation;
  public String renderorder;
  public String tiledversion;
  public int tileheight;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("tileset")
  public Tileset[] tilesets;

  public int tilewidth;
  public String type;
  public int width;
}
