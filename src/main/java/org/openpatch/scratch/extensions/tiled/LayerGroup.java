package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

/** A group layer: tile layers, object layers and further groups. */
class LayerGroup {
  public String name;
  public boolean visible = true;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("layer")
  public MapLayer[] layers;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("objectgroup")
  public ObjectGroup[] objectGroups;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("group")
  public LayerGroup[] groups;
}
