package org.openpatch.scratch.tiled;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

class Map {
  @JacksonXmlElementWrapper(localName = "properties")
  @tools.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "property")
  public java.util.List<Property> properties;

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

  /** Group layers (Tiled 1.2+); their layers count as the map's own. */
  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("group")
  public LayerGroup[] groups;
}
