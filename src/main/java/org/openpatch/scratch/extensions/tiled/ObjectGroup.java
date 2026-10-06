package org.openpatch.scratch.extensions.tiled;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

class ObjectGroup {
  public double height;
  public int id;
  public String name;
  public double rotation;
  public String type;

  /** Whether the objects' y already points up (see TiledMap.getObjectsFromLayer). */
  @com.fasterxml.jackson.annotation.JsonIgnore boolean stageCoordinates;

  /** Tiled 1.9+ calls a layer's type its class. */
  @com.fasterxml.jackson.annotation.JsonProperty("class")
  public void setClassName(String className) {
    if (this.type == null || this.type.isEmpty()) {
      this.type = className;
    }
  }
  public boolean visible;
  public double width;
  public double x;
  public double y;

  @JacksonXmlElementWrapper(useWrapping = false)
  @JsonProperty("object")
  public MapObject[] objects;
}
